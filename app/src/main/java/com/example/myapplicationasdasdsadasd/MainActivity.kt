package com.example.myapplicationasdasdsadasd

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import com.itextpdf.text.pdf.AcroFields
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    val REQUEST_CODE_PICK_FILE = 123
    private var inputStream: InputStream? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.pdfsuda)
        btn.setOnClickListener {
               val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val uri: Uri? = data?.data
            uri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val filename = getFileName(uri)
                findViewById<TextView>(R.id.textview_first).text = filename.toString()
                val pdfReader = PdfReader(inputStream)
                val pageText = PdfTextExtractor.getTextFromPage(pdfReader, 1)
                var qOptions = mutableListOf<String>()

                val lines = pageText.split("\n")
                val questions = mutableListOf<String>()

                val fields = pdfReader.acroFields
                val radioGroups = fields.getFields()
                    .filter { fields.getFieldType(it.key) == AcroFields.FIELD_TYPE_RADIOBUTTON }
                for ((groupName, _) in radioGroups) {
                    // Get the selected radio button value for the group
                    val value = fields.getField(groupName)

                    // Get the correct answer for the group

                    // Compare the selected value to the correct answer and print the result
                    println("The answer for group $groupName is correct")
                    println("The answer for group $groupName is incorrect")
                }


                var mi = 0
                while (mi < lines.size) {
                    if (lines[mi].trim().matches(Regex("Question \\d+"))) {

                        mi = mi + 3
                        var question = lines[mi]
                        if (lines[mi].startsWith("Complete") || lines[mi].startsWith("Mark") || lines[mi].startsWith(
                                "a. "
                            ) || lines[mi].startsWith("b. ") || lines[mi].startsWith("c. ") || lines[mi].startsWith(
                                "d. "
                            )
                        ) {
                        } else {
                            mi = mi + 1
                            if (lines[mi].startsWith("a. ")) {
                            }
                            question = question + " " + lines[mi]
                            Log.d("mimino", question)
                        }
                    } else {
                        if (lines[mi].startsWith("a. ") || lines[mi].startsWith("b. ") || lines[mi].startsWith(
                                "c. "
                            ) || lines[mi].startsWith("d. ")
                        ) {

                            Log.d("miminoAnswers", lines[mi])
                            mi = mi + 1
                        } else mi = mi + 1
                    }
                }


                for (q in questions) {
                    Log.d("QQQ", q)
                }



                pdfReader.close()
                inputStream?.close()
                // TODO: Do something with the page text
            }
        }
    }

    fun handleSelectedFile(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val document = PDDocument.load(inputStream)
        // process the PDF document

        document.close()
    }


    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }


}