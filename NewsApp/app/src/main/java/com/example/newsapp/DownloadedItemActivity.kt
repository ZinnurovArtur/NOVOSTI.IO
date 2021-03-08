package com.example.newsapp

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_newsfragment.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Download activity with recyclview
 */

class DownloadedItemActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()


    /**
     * Initialise the activity and populate the list of recyclview items
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.donloaded_recyclview)
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView2)
        for (i in getExternal()) {
            Log.i("files", i.fileName.toAbsolutePath().toString())
        }
        applYrecycle(recyclerView, getExternal())

        /**
         * Swiped listener to delete the news internally on phone
         */
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = recyclerView.adapter as AdapterDownload
                    val path = adapter.geturi(viewHolder.adapterPosition)
                    val fileTodelete = File(path.toUri())
                    fileTodelete.delete()

                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    /**
     * Get external list of  saved newsdata items from the internal system
     * @return arraylist of uri paths
     */
    fun getExternal(): ArrayList<Path> {


        var listOfpdfs = ArrayList<Path>()
        val downloadFolder = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        var recourcePath = Paths.get(downloadFolder?.path.toString())
        Log.i("recource path", recourcePath.toString())


        Files.walk(recourcePath)
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().endsWith("ID" + mAuth.currentUser?.uid + ".pdf") }
            .forEach { listOfpdfs.add(it) }

        return listOfpdfs

    }

    /**
     * Apply changes of recyclview and setdata
     */
    fun applYrecycle(view: RecyclerView, listdownload: ArrayList<Path>) {

        view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = AdapterDownload(listdownload)
        }
    }


}