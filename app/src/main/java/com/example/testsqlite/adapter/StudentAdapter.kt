package com.example.testsqlite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testsqlite.R
import com.example.testsqlite.common.OnItemClickListener
import com.example.testsqlite.data.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_student.*

class StudentAdapter(
    ctx: Context,
    private val list: MutableList<Student>
) : RecyclerView.Adapter<StudentAdapter.VH>() {
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(ctx) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = inflater.inflate(R.layout.item_student, parent, false)
        return VH(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = list[position]
        holder.onBind(it)
    }

    fun updateData(newList: List<Student>) {
        val oldSize = list.size
        list.clear()
        notifyItemRangeRemoved(0, oldSize)
        list.addAll(newList)
        notifyItemRangeInserted(0, newList.size)
    }

    fun addItem(student: Student) {
        list += student
        notifyItemInserted(list.size)
    }

    fun updateItem(student: Student) {
        val it: Student = list.first { it.id == student.id }
        list.remove(it)
        notifyItemRemoved(list.indexOf(it))
        list += student
        notifyItemInserted(list.size - 1)

    }

    fun getItemByPosition(pos: Int): Student = list[pos]

    fun removeItByPosition(pos: Int) {
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    class VH(
        override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        private var lastStudent: Student? = null

        fun onBind(it: Student) {
            lastStudent = it
            tv_name.text = it.name
            tv_age.text = "${it.age}"
        }
    }
}