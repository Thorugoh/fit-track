package com.hfa.fitnesstracker

import android.content.DialogInterface.OnClickListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfa.fitnesstracker.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity() {
    private  lateinit var rvMain: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        val result = mutableListOf<Calc>()
        val adapter = MainAdapter(result)

        rvMain = findViewById(R.id.rv_calc)
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapter

        val type = intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")

        Thread {
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)

            runOnUiThread(){
                result.addAll(response)
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private inner class MainAdapter(
        private val calcItems: List<Calc>
    ) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

        private inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc, position: Int) {
                val tv = itemView as TextView
                val result = mutableListOf<Calc>()
                val adapter = MainAdapter(result)

                tv.setOnClickListener {
                    AlertDialog.Builder(this@ListCalcActivity)
                        .setMessage("O que deseja fazer?")
                        .setPositiveButton("Atualizar"){ dialog, wich ->
                            Thread {
                                when (item.type) {
                                    "imc" -> {
                                        val intent = Intent(this@ListCalcActivity, IMCActivity::class.java)
                                        intent.putExtra("updateId", item.id)
                                        startActivity(intent)
                                    }
                                    "tmb" -> {
                                        val intent = Intent(this@ListCalcActivity, TmbActivity::class.java)
                                        intent.putExtra("updateId", item.id)
                                        startActivity(intent)
                                    }
                                }
                            }.start()
                        }
                        .setNegativeButton("Deletar"){ dialog, wich ->
                            Thread {
                                val app = (application as App)
                                val dao = app.db.calcDao()
                                dao.deleteRegister(item)

                                runOnUiThread {
                                    result.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                }

                            }.start()

                        }
                        .create()
                        .show()
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val date = sdf.format(item.createdDate)
                val res = item.res

                tv.text = getString(R.string.list_response, res, date)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return MainViewHolder(view)
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val itemCurrent = calcItems[position]
            holder.bind(itemCurrent, position)
        }

        override fun getItemCount(): Int {
            return calcItems.size
        }
    }
}