package com.example.newsapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.nio.file.Path

/**
 * Adapter for populating the list of downloaded news
 */
class AdapterDownload(private val listDownload: MutableList<Path>) :
    RecyclerView.Adapter<AdapterDownload.ViewHolder>() {

    /**
     * Initialise the viewHolder
     *
     *
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDownload.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemrecyclviewdownload, parent, false)
        return ViewHolder(view)
    }
    /**
     * Bind the specific element of downloaded  to the place and shows the in right order sequence
     * @param holder holder of recyclview
     * @param position specific place
     */
    override fun onBindViewHolder(holder: AdapterDownload.ViewHolder, position: Int) {
        holder.bind(listDownload[position])
    }

    /**
     * Size of donload list
     * @return int size
     */
    override fun getItemCount(): Int {
        return listDownload.size
    }

    /**
     * Remove download element at specific position
     */
    fun removeAt(position: Int) {
        listDownload.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Get uri of the file at the specific position of element
     * @return Path
     */
    fun geturi(position: Int): Path {
        val path = listDownload.get(position)
        return path
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title = itemView.findViewById<TextView>(R.id.text_download)
        var card = itemView.findViewById<CardView>(R.id.card_download)
        lateinit var pathTogo: Path

        /**
         * Put the title text nad save the path
         */
        fun bind(path: Path) {
            title.text = path.fileName.toString()
            pathTogo = path

        }

        init {
            card.setOnClickListener(this)
        }

        /**
         * Open pdf provider when user clicks
         */
        override fun onClick(p0: View?) {
            val uri = p0?.let {
                FileProvider.getUriForFile(
                    it.context,
                    BuildConfig.APPLICATION_ID + ".provider", pathTogo.toFile()
                )
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setDataAndType(uri, "application/pdf")

            p0?.context?.startActivity(intent)


        }


    }


}



