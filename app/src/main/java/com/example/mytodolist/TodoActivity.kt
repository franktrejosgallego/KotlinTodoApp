package com.example.mytodolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.databinding.ActivityTodoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.list_item_view.view.*

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var database: DatabaseReference
    private lateinit var taskReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recycler: RecyclerView
    private var adapter: TaskAdapter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo)
        auth = Firebase.auth
        database = Firebase.database.reference
        val user = auth.currentUser!!.uid
        taskReference =  FirebaseDatabase.getInstance().getReference("user-tasks").child(user)
        recycler = findViewById(R.id.task_list)
        recycler.layoutManager = LinearLayoutManager(this)

        //Floating action button start
        binding.floatingActionButton.setOnClickListener{view: View ->

           val rootlayout = layoutInflater.inflate(R.layout.custompopup, null)

            val task_name = rootlayout.findViewById<EditText>(R.id.TaskName)
            val task_description = rootlayout.findViewById<EditText>(R.id.TaskDescription)
            val close_button = rootlayout.findViewById<Button>(R.id.CloseButton)
            val add_button = rootlayout.findViewById<Button>(R.id.AddButton)

            //Pop window object
            val popupWindow = PopupWindow(
                rootlayout,
                LinearLayout.LayoutParams.WRAP_CONTENT ,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
            )

            popupWindow.update();
            popupWindow.setElevation(10.5F)
            popupWindow.showAtLocation(

                ToDoActivity, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )

            close_button.setOnClickListener{
                popupWindow.dismiss()
            }

            add_button.setOnClickListener{

                var name = task_name.text.toString()
                var description = task_description.text.toString()
                val userId = user
                if(userId!= null){
                writeNewTask(userId,name, description)}
                popupWindow.dismiss()
            }

        }// end of floating action

    }// end of oncreate-----------------------------------------------------------


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sign_out -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            FirebaseAuth.getInstance().signOut()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun writeNewTask(userId: String, taskname: String, taskdescription: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = database.child("posts").push().key
        if (key == null) {
            Log.w("TodoActivity", "Couldn't get push key for posts")
            return
        }

        val newtask = Tasks(userId, taskname, taskdescription)
        val taskValues = newtask.toMap()
        val childUpdates = hashMapOf<String, Any>(
         //*   "/tasks/$key" to taskValues,
            "/user-tasks/$userId/$key" to taskValues
        )

        database.updateChildren(childUpdates)

    }

    public override fun onStart() {
        super.onStart()
        adapter = TaskAdapter(this, taskReference)
        recycler.adapter = adapter
    }




//-----------------------------Adapter Class-----------------------------------------------
    private class TaskAdapter(private val context: Context, private val databaseReference: DatabaseReference): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        private val childEventListener: ChildEventListener?
        private val taskIds = ArrayList<String>()
        private val tasks = ArrayList<Tasks>()

        init {

            // Create child event listener
            // [START child_event_listener_recycler]
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("GetData", "onChildAdded:" + dataSnapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val taskreceived = dataSnapshot.getValue<Tasks>()

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    taskIds.add(dataSnapshot.key!!)
                    tasks.add(taskreceived!!)
                    notifyItemInserted(tasks.size - 1)
                    // [END_EXCLUDE]
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("GetData", "onChildChanged: ${dataSnapshot.key}")

//                    // A comment has changed, use the key to determine if we are displaying this
//                    // comment and if so displayed the changed comment.
//                    val newComment = dataSnapshot.getValue<Tasks>()
//                    val commentKey = dataSnapshot.key
//
//                    // [START_EXCLUDE]
//                    val commentIndex = taskIds.indexOf(commentKey)
//                    if (commentIndex > -1 && newComment != null) {
//                        // Replace with the new data
//                        tasks[commentIndex] = newComment
//
//                        // Update the RecyclerView
//                        notifyItemChanged(commentIndex)
//                    } else {
//                        Log.w("GetData", "onChildChanged:unknown_child: $commentKey")
//                    }
                    // [END_EXCLUDE]
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d("GetData", "onChildRemoved:" + dataSnapshot.key!!)

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    val commentKey = dataSnapshot.key

                    // [START_EXCLUDE]
                    val commentIndex = taskIds.indexOf(commentKey)
                    if (commentIndex > -1) {
                        // Remove data from the list
                        taskIds.removeAt(commentIndex)
                        tasks.removeAt(commentIndex)

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex)
                    } else {
                        Log.w("GetData", "onChildRemoved:unknown_child:" + commentKey!!)
                    }
                    // [END_EXCLUDE]
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                    Log.d("GetData", "onChildMoved:" + dataSnapshot.key!!)
//
//                    // A comment has changed position, use the key to determine if we are
//                    // displaying this comment and if so move it.
//                    val movedComment = dataSnapshot.getValue<Tasks>()
//                    val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("GetData", "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(context, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show()
                }


            }
            databaseReference.addChildEventListener(childEventListener)
            // [END child_event_listener_recycler]
            // Store reference to listener so it can be removed on app stop
            this.childEventListener = childEventListener

        } //end of Init()


        class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val auth = Firebase.auth
            val user = auth.currentUser!!.uid
            val taskReference =  FirebaseDatabase.getInstance().getReference("user-tasks").child(user)

            fun bind(task: Tasks, taskId: String) {
                itemView.taskName.text = task.taskname
                itemView.taskDescription.text = task.taskdescription
                val item = taskId
                itemView.deleteButton.setOnClickListener{
                    val value= taskReference.child(taskId)
                    value.removeValue()
                    Log.i("TodoActivity","VALUE Deleted")
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.list_item_view, parent, false)
            return TaskViewHolder(view)

        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(tasks[position], taskIds[position])
            holder.itemView.checkBoxItem.setOnClickListener{
            }
        }

        override fun getItemCount(): Int = tasks.size

        fun cleanupListener() {
            childEventListener?.let {
                databaseReference.removeEventListener(it)
            }
        }
 } // end of adapter class

} // end of Todo Activity


// Task data class
data class Tasks(

    var uid: String? ="",
    var taskname: String? ="",
    var taskdescription: String? = ""
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "uid" to uid,
            "taskname" to taskname,
            "taskdescription" to taskdescription
        )
    }

}

