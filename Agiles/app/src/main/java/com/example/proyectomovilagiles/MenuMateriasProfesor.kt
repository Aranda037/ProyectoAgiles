package com.example.proyectomovilagiles

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_menu_materias_profesor.*
import kotlinx.android.synthetic.main.materia_cardview.view.*
import objetos.Materia

class MenuMateriasProfesor : AppCompatActivity() {

    var listaMaterias = ArrayList<Materia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_materias_profesor)

        val preferencias = MyPreference(this)

        crearMaterias()

        var adaptador = AdaptadorMateria(this, listaMaterias)
        listview.adapter = adaptador

        btnSalir.setOnClickListener {
            preferencias.setPass("")
            preferencias.setUser("")
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

    }

    fun crearMaterias(){
        var materia1 = Materia("Materia1", "Hoy", "Hora Actual", "1800s", R.drawable.ic_backgroundtest)
        var materia2 = Materia("Materia2", "Manana", "Hora Actual", "1800s", R.drawable.ic_backgroundtest)

        listaMaterias.add(materia1)
        listaMaterias.add(materia2)
    }

    private class AdaptadorMateria: BaseAdapter {

        var contexto: Context? = null
        var materias = ArrayList<Materia>()

        constructor(context: Context, materias: ArrayList<Materia>){
            contexto = context
            this.materias = materias
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.materia_cardview, null)
            var materia = materias[position]


            //TODO("Agregar una imagen de fondo para la carta, separada de la materia")
            vista.card.setBackgroundResource(materia.imagen!!)
            vista.materia_foto.setImageResource(materia.imagen!!)
            vista.materia_nombre.text = materia.nombre
            vista.materia_fecha.text = "miaw"
            vista.materia_hora.text = "miaw"
            vista.materia_salon.text = materia.salon

            vista.setOnClickListener{
                //TODO("Call next activity")
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return materias[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return materias.size
        }


    }
}
