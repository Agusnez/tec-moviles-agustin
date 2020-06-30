package us.example.entregable1


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filter.*
import us.example.entregable1.items.dummy.DummyContent
import us.example.entregable1.ui.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.*


class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val format = SimpleDateFormat("dd/MM/yyyy")

        startDate.setText("${format.format(DummyContent.startDate.time)}")
        endDate.setText("${format.format(DummyContent.endDate.time)}")

        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }

        endDate.setOnClickListener {
            showDatePickerDialog(endDate)
        }

        if (DummyContent.COLUMNS == 2) {
            switch_columns.isChecked = true
        }

        switch_columns.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                DummyContent.COLUMNS = 2
            } else {
                DummyContent.COLUMNS = 1
            }

        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finish()
    }

    private fun showDatePickerDialog(editText: EditText) {

        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val dayStr = day.twoDigits()
            val monthStr = (month + 1).twoDigits() // +1 because January is zero

            val selectedDate = "$dayStr/$monthStr/$year"

            val newDate = Calendar.getInstance()
            newDate.set(year,month,day)
            val format = SimpleDateFormat("yyyy-MM-dd")


            if (editText.equals(startDate)) {
                if (newDate.time > DummyContent.endDate.time) {
                    Toast.makeText(applicationContext, "No se puede filtrar por una fecha posterior a la de fin", Toast.LENGTH_SHORT).show()
                } else {
                    editText.setText(selectedDate)
                    DummyContent.startDate.set(year,month,day)
                    Toast.makeText(applicationContext, "Filtrando contenido desde ${format.format(DummyContent.startDate.time)}", Toast.LENGTH_SHORT).show()
                }

            } else if (editText.equals(endDate)) {
                if (newDate.time < DummyContent.startDate.time) {
                    Toast.makeText(applicationContext, "No se puede filtrar por una fecha anterior a la de inicio", Toast.LENGTH_SHORT).show()
                } else {
                    editText.setText(selectedDate)
                    DummyContent.endDate.set(year,month,day)
                    Toast.makeText(applicationContext, "Filtrando contenido hasta ${format.format(DummyContent.endDate.time)}", Toast.LENGTH_SHORT).show()
                }


            }

        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()

}
