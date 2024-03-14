package com.pratik.agricole

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pratik.agricole.DTO.ToDo
import com.pratik.agricole.databinding.FragmentTasksBinding


class Tasks : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    lateinit var dbHandler: DBHandler
    val options = arrayOf(
        "Water Farm", "Seed the Wheat", "Add fertilizer to Rice Farm", "Water Tomato Field"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireActivity() fragment
        _binding =  FragmentTasksBinding.inflate(inflater, container, false)

        dbHandler = DBHandler(requireActivity())
        binding.rvDashboard.layoutManager = LinearLayoutManager(requireActivity())

        binding.fabDashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.setTitle("Add ToDo")

            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val spinner = view.findViewById<Spinner>(R.id.spinner_todo_options)

            // Define your dropdown options

            // Create an ArrayAdapter using the string array and a default spinner layout
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spinner.adapter = adapter

            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                val selectedOption = spinner.selectedItem.toString()
                // Now you have the selected option, you can do whatever you want with it
                // For now, let's just show it in a Toast
//                Toast.makeText(requireContext(), "Selected option: $selectedOption", Toast.LENGTH_SHORT).show()
                val toDo = ToDo()
                toDo.name = selectedOption
                dbHandler.addToDo(toDo)
                refreshList()
                // You can add your database handling logic here
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

        return binding.root
    }

    fun updateToDo(toDo: ToDo){
        val dialog = AlertDialog.Builder(requireActivity())
        dialog.setTitle("Update ToDo")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val spinner = view.findViewById<Spinner>(R.id.spinner_todo_options)

        // Define your dropdown options

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set the selected value of the spinner to the current value of the ToDo item
        val selectedIndex = options.indexOf(toDo.name)
        spinner.setSelection(selectedIndex)

        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            val selectedOption = spinner.selectedItem.toString()
            // Now you have the selected option, you can do whatever you want with it
            // For now, let's just show it in a Toast
//            Toast.makeText(requireContext(), "Selected option: $selectedOption", Toast.LENGTH_SHORT).show()

            toDo.name = selectedOption
            dbHandler.updateToDo(toDo)

            // Refresh the list
            refreshList()
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }


    override fun onResume() {
        refreshList()
        super.onResume()
    }


    private fun refreshList(){
        binding.rvDashboard.adapter = DashboardAdapter(requireActivity(), this, dbHandler.getToDos())
    }



    class DashboardAdapter(val activity1: FragmentActivity, val activity: Tasks, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity1).inflate(R.layout.rv_child_dashboard, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = list[p1].name

            holder.toDoName.setOnClickListener {
                val intent = Intent(activity1,ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID,list[p1].id)
                intent.putExtra(INTENT_TODO_NAME,list[p1].name)
                activity.startActivity(intent)
            }

            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity1,holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    when(it.itemId){
                        R.id.menu_edit->{
                            activity.updateToDo(list[p1])
                        }
                        R.id.menu_delete->{
                            val dialog = AlertDialog.Builder(activity1)
                            dialog.setTitle("Are you sure")
                            dialog.setMessage("Do you want to delete this task ?")
                            dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                            }
                            dialog.show()
                        }
                        R.id.menu_mark_as_completed->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,true)
                        }
                        R.id.menu_reset->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,false)
                        }
                    }

                    true
                }
                popup.show()
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.tv_todo_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}