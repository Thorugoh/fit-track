package com.hfa.fitnesstracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.hfa.fitnesstracker.model.Calc

class TmbActivity : AppCompatActivity() {

    private lateinit var lifestyle: AutoCompleteTextView
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var editAge: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmb)

        editWeight = findViewById(R.id.edit_tmb_weigth)
        editHeight = findViewById(R.id.edit_tmb_height)
        editAge = findViewById(R.id.edit_tmb_age)

        lifestyle = findViewById(R.id.auto_lifestyle)

        val items = resources.getStringArray(R.array.tmb_lifestyle)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lifestyle.setText(items.first())
        lifestyle.setAdapter(adapter)

        val btnSend = findViewById<Button>(R.id.btnc_tmb_send)
        btnSend.setOnClickListener {
            if (!validate()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()
            val age = editAge.text.toString().toInt()

            val result = calculateTbm(weight, height, age)
            val response = tmbRequest(result)

            AlertDialog.Builder(this)
                .setMessage(getString(R.string.tmb_response, response))
                .setPositiveButton(android.R.string.ok){ dialog, wich ->
                }
                .setNegativeButton(R.string.save){ dialog, wich ->
                    Thread {
                        val app = (application as App)
                        val dao = app.db.calcDao()
                        dao.insert(Calc(type = "tmb", res = result))

                        runOnUiThread {
                            openListActivity()
                        }

                    }.start()

                }
                .create()
                .show()

            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search){
            finish()
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openListActivity() {
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type", "tmb")
        startActivity(intent)
    }

    fun tmbRequest(tmb: Double) : Double {
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        return if(lifestyle.text.toString() == items[0]) {
            tmb * 1.2
        }else if (lifestyle.text.toString() == items[1]) {
            tmb * 1.375
        }else if (lifestyle.text.toString() == items[2]){
            tmb * 1.55
        }else if(lifestyle.text.toString() == items[3]){
            tmb * 1.725
        }else if(lifestyle.text.toString() == items[4]){
            tmb * 1.9
        }else 0.0

    }

    fun calculateTbm(weight: Int, height: Int, age: Int ): Double {
        return 66 + (13.8 * weight) + (5 * height) - (6.8 * age)
    }



    private fun validate(): Boolean {
        return (editWeight.text.toString().isNotEmpty()
                && editHeight.text.toString().isNotEmpty()
                && editHeight.text.toString().isNotEmpty()
                && editAge.text.toString().isNotEmpty()
                && !editWeight.text.toString().startsWith("0")
                && !editHeight.text.toString().startsWith("0")
                && !editAge.text.toString().startsWith("0"))
    }
}