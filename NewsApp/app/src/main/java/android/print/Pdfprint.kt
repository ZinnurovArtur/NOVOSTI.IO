package android.print

import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.File
import java.io.FileOutputStream

/**
 * This class is responsible  for the setting the file internaly on the device
 */
class PdfPrint(private val printAttributes: PrintAttributes) {

    /**
     * @param printAdapter adapter for document process
     * @param path to put the file
     * @param fileName name of the file
     */
    fun print(printAdapter: PrintDocumentAdapter, path: File, fileName: String) : Boolean{
        var status = false

        printAdapter.onLayout(null, printAttributes, null, object : LayoutResultCallback() {
            override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {

                printAdapter.onWrite(
                        arrayOf(PageRange.ALL_PAGES),
                        getOutputFile(path, fileName),
                        CancellationSignal(),
                        object : WriteResultCallback() {
                            override fun onWriteFinished(pages: Array<PageRange>) {
                                super.onWriteFinished(pages)
                                status = true
                            }
                        })





            }
        }, null)

        return status


    }

    /**
     * Create a file and save it on path
     * @param path extracted File for saving
     * @param fileName name of the file
     */
    private fun getOutputFile(path: File, fileName: String): ParcelFileDescriptor? {
        if (!path.exists()) {
            path.mkdirs()
        }
        val file = File(path, fileName)

        try {
            file.createNewFile()


            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            Log.e("msg", "Failed to open ParcelFileDescriptor", e)
        }
        return null
    }




}