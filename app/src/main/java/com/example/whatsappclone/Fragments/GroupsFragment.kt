package com.example.whatsappclone.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.whatsappclone.GroupChatActivity

import com.example.whatsappclone.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class GroupsFragment : Fragment() {


    val db by lazy {
        FirebaseDatabase.getInstance()
            .reference.child("Groups")
    }

    lateinit var  list_of_groups: MutableList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val group= inflater.inflate(R.layout.fragment_groups, container, false)
        list_of_groups= arrayListOf()
      //  RetrieveAndDisplayGroups()

//       lv.setOnItemClickListener { parent, view, position, id ->
//            var currGroupName=parent.getItemAtPosition(position).toString()
//           val i=Intent(context,GroupChatActivity::class.java)
//           i.putExtra("groupName",currGroupName)
//           startActivity(i)
//         }

        return group
    }



    private fun RetrieveAndDisplayGroups() {

        var arrayAdapter= ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, list_of_groups)
        lv.adapter=arrayAdapter

        db.child("groupName").addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                if (dataSnapshot.exists())

                    for (i in dataSnapshot.children.iterator()){
                        val x=i.value.toString()
                        list_of_groups.add(x)
                        arrayAdapter.notifyDataSetChanged()
                    }

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }//
}
//        db.child("Groups").addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(databaseError: DatabaseError) {
//            }
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot?.exists()){
//
//                    for (i in dataSnapshot.children.iterator()){
//                        val x=i.value.toString()
//                        list_of_groups.add(x!!)
//                    }
//                    val arrayAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_expandable_list_item_1, list_of_groups)
//                    lv.adapter = arrayAdapter
//                    //list_of_groups.clear()
//                    arrayAdapter.notifyDataSetChanged()
//                }
//            }
//        })

