package com.example.proyectomovilagiles.Clases

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proyectomovilagiles.R
import com.example.proyectomovilagiles.getHoraActual
import com.example.proyectomovilagiles.legacy.AsistenciaAlumno
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.Result
import dataBaseObjects.DAOAsistencias
import kotlinx.android.synthetic.main.activity_lista_clases.*
import kotlinx.android.synthetic.main.activity_lista_clases_alumno.*
import kotlinx.android.synthetic.main.llenar_clases.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import objetos.Alumno
import objetos.Asistencia
import objetos.Clase

class ListaClasesAlumno : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var clases = ArrayList<Clase>()
    var alumno = Alumno()
    var mScanner : ZXingScannerView? = null

    val retardoTiempo = 5
    var faltaTiempo = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_clases_alumno)

        var materia = intent.getStringExtra("materia")
        //clases = intent.getSerializableExtra("clases") as ArrayList<Clase>
        alumno = intent.getSerializableExtra("id") as Alumno
        var adaptador = AdaptadorClases(this, clases,alumno!!.nombre)
        listasClasesAlumno.adapter = adaptador

        btnAsistencia.setOnClickListener {
            mScanner = ZXingScannerView(this)
            setContentView(mScanner)
            mScanner?.setResultHandler(this)
            mScanner?.startCamera()
        }
    }

    fun llenarClases(mat:String){
        val database = FirebaseDatabase.getInstance()
        val referencia = database.getReference("Materias").child(mat).child("Clases")
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var children = p0.children


                    for (child in children) {

                    }
                }
            }
        })
    }

    override fun handleResult(p0: Result?) {

        Log.v("HanlderResult",p0?.text)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Clase")
        builder.setMessage(p0?.text)
        val dialogo = builder.create()
        dialogo.show()
        val text = p0?.text.toString()

        val codigo = text.split(".").toTypedArray()
        println("VOY A IMPRIMIR")
        var materia = codigo[0]
        var clase = codigo[1]
        var estado = codigo[2]
        println(materia)
        println(clase)
        println(estado)

        //Separamos la hora del QR que viene en el estado
        val estadoSplitted = estado.split(":")
        //Multiplicamos las horas por 60 para obtener los minutos y los agregamos a los minutos de la hora
        val horaEstadoInt = (estadoSplitted.get(0).toInt() * 60) + (estadoSplitted.get(1).toInt())


        val horaActual = getHoraActual()
        //Separamos la hora actual
        val horaActualSplitted = horaActual.split(":")
        //Multiplicamos las horas por 60 para obtener los minutos y los agregamos a los minutos de la hora
        val horaActualInt = (horaActualSplitted.get(0).toInt() * 60) + (horaActualSplitted.get(1).toInt())

        var estadoAsistencia:Int? = null

        //Si la hora actual de registro menos la hora del QR, es menor al tiempo definido para el retardo
        if((horaActualInt - horaEstadoInt) <= retardoTiempo){
            //Lo registramos como asistencia
            estadoAsistencia = 1
        } else if ((horaActualInt - horaEstadoInt) <= faltaTiempo){
            //En caso de que la hora actual de registro sea menor al tiempo definido para la falta
            //Lo tomamos como retardo
            estadoAsistencia = 0
        } else {
            //En caso de que la hora si fuese mayor al definido para la falta
            //Se tomara como falta
            estadoAsistencia = -1
        }

        //Creamos la asistencia
        var asistencia = Asistencia(alumno,estadoAsistencia!!, getHoraActual())

        DAOAsistencias.registrarAsistencia(materia,clase,asistencia)
        dialogo.dismiss()
        finish()
    }

    private class AdaptadorClases : BaseAdapter {

        var context: Context
        var clases: ArrayList<Clase>? = null
        var id  = ""

        constructor(context: Context, clases: ArrayList<Clase>, nom:String) {
            this.context = context
            this.clases = clases
            id = nom
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var layout = LayoutInflater.from(context)
            var vista = layout?.inflate(R.layout.llenar_clases, null)!!
            var cla = clases!![position]

            if (vista != null) {
                vista.diaClase.text = cla.dia.diaSemana
                vista.horas.text = cla.dia.ini
                vista.salonClase.text = cla.salon
                vista.txtFecha.text = cla.fecha
            }

            vista.setOnClickListener {
                val intent = Intent(context, AsistenciaAlumno::class.java)
                var auxiliar = Asistencia()
                for(x in cla.asistencias){
                    if(x.alumno.id.equals(id)){
                        auxiliar = x
                    }
                }
                intent.putExtra("asist",auxiliar)
                (context as Activity).startActivity(intent)
            }
            return vista

        }

        override fun getItem(position: Int): Any {
            return clases?.get(position) ?: "Error"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return clases?.size ?: 0
        }
    }
}
