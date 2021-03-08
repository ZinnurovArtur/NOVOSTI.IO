package com.example.newsapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_scrolling.*
import models.Newsdata


/**
 * Class for creting account fragment
 */
class AccountFragment : Fragment() {
    private var mAuth = FirebaseAuth.getInstance()
    private var databse = Firebase.database.reference
    private var storage = Firebase.storage.reference
    private lateinit var imageView: ImageView
    private var imagePath = Uri.EMPTY

    /**
     * Standard onCreatview when any actions happens
     */
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_scrolling, container, false)

        return view

    }

    /**
     *Create view of account
     * @param view to creat
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(view, "Swipe left to delete topic ", Snackbar.LENGTH_LONG).show()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleViewTopics)
        val account_image = view.findViewById<ImageView>(R.id.account_picture)
        val accountEmail = view.findViewById<TextView>(R.id.accEmail)

        imageView = account_image
        accountEmail.text = getDatauser(imageView)

        loadPicture(account_image)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        loadTopics(recyclerView)

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                /**
                 * On swiped method allows to swipe element of recyclview and to delete from phone and Firebase
                 * @param viewHolder of the current recyclview
                 *
                 */
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = recycleViewTopics.adapter as AdapterAccount
                    val userdata = adapter.getUserdata(viewHolder.adapterPosition)
                    Log.i("position", userdata.topic.toString())
                    mAuth.currentUser?.uid?.let { it2 ->
                        databse.child("usersNotif").child(it2).child("notif").child(userdata.topic.toString())
                            .removeValue()
                        databse.child("users").child(it2).child("listOftopics").child(userdata.topic.toString())
                            .removeValue()
                    }
                    Snackbar.make(view, "Item removed", Snackbar.LENGTH_LONG).show()
                    adapter.removeAt(viewHolder.adapterPosition)

                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycleViewTopics)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    /**
     * Get the data for the current user by using async call
     * @param imageView to put image avatar
     */
    private fun getDatauser(imageView: ImageView): String {

        val user = mAuth.currentUser

        var email: String = ""
        if (user != null) {
            email = user.email.toString().substringBeforeLast("@") + "@"
            var storageImg = mAuth.currentUser?.let {
                storage.child(it.uid).child("images/Profile_img")
                    .downloadUrl.addOnCompleteListener(object : OnCompleteListener<Uri> {
                        override fun onComplete(p0: Task<Uri>) {
                            context?.let { it1 ->
                                if (p0.isSuccessful) {
                                    GlideApp.with(it1.applicationContext).load(p0.result.toString()).apply(
                                        RequestOptions()
                                            .transforms(CenterCrop(), RoundedCorners(90))
                                    )
                                        .into(imageView)


                                }
                            }
                        }


                    }).addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            if (p0 is StorageException &&
                                (p0 as StorageException).errorCode == StorageException.ERROR_OBJECT_NOT_FOUND
                            ) {
                                context?.let { it1 ->
                                    /**
                                     * If failed load the basic picture
                                     */
                                    GlideApp.with(it1.applicationContext).load(
                                        "https://toppng.com/uploads/preview/" +
                                                "app-icon-set-login-icon-comments-avatar-icon-11553436380yill0nchdm.png"
                                    ).apply(
                                        RequestOptions()
                                            .transforms(CenterCrop(), RoundedCorners(90))
                                    )
                                        .into(imageView)
                                }

                                Log.i("Error storage", "File not exist");

                            }
                        }
                    })


            }

        }






        return email


    }

    /**
     * Async call for the populating the recyclview. Getting the data from firebase
     * @param view current recyclview
     */
    private fun loadTopics(view: RecyclerView) {
        val listNews = ArrayList<Newsdata>()

        mAuth.currentUser?.uid?.let { it1 ->
            databse.child("users").child(it1).child("listOftopics").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        var topic = ds.child("topic").value
                        var url = ds.child("urlImg").value
                        listNews.add(Newsdata(url.toString(), topic.toString(), ""))

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Topics Cancelled", "Cancelled");
                }

            })

            view.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = AdapterAccount(listNews)

            }


        }
    }

    /**
     * Call the photo library from phone
     * @param imageview to put picture
     */
    private fun loadPicture(view: ImageView) {
        view.setOnClickListener {
            var profileIntent = Intent()
            profileIntent.setType("image/*")
            profileIntent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(profileIntent, "Select image"), PICK_IMG)
        }

    }

    /**
     * Import override method for calling the library photo. With handling the responses
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PICK_IMG && resultCode == RESULT_OK && data.data != null) {
                var uri = data.data
                if (uri != null) {
                    imagePath = uri
                    Log.i("msg", imagePath.path.toString())
                    applyImage(imageView, uri)
                    userPicdatabs(uri)

                }


            }

        }

    }

    /**
     * Upload picture avatar to firebase
     */
    private fun userPicdatabs(uri: Uri) {
        var storageImg = mAuth.currentUser?.let {
            storage
                .child(it.uid).child("images")
                .child("Profile_img")
        }
        storageImg?.putFile(uri)
    }

    /**
     * Apply image after user has chosen from photo library  folder
     */
    private fun applyImage(view: ImageView, uri: Uri) {

        GlideApp.with(this).load(uri).apply(
            RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(90))
        )
            .into(view)
    }

    companion object {
        val PICK_IMG = 123
    }


}
