package com.example.testsqlite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testsqlite.R
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

    class VH(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun onBind(it: Student) {
            tv_name.text = it.name
            tv_age.text = "${it.age}"
        }
    }
}