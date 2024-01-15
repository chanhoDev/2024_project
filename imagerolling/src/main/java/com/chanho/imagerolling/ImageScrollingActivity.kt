package com.chanho.imagerolling

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chanho.common.Util.smoothScrollToPositionWithDelay
import com.chanho.imagerolling.databinding.ActivityImageScrollingBinding
import com.chanho.imagerolling.databinding.EventItemViewBinding

class ImageScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageScrollingBinding
    private var pos = 0

    var doHandler:Handler?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

    override fun onStart() {
        super.onStart()
        val adapter = Example3EventsAdapter{
            Toast.makeText(this@ImageScrollingActivity,it,Toast.LENGTH_SHORT).show()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = linearLayoutManager
        val list = mutableListOf<String>("1","2","3","4")
        adapter.events.addAll(list)
        adapter.notifyDataSetChanged()
//        val thread = Thread{
//            doHandler = Handler(Looper.getMainLooper())
//
//            doHandler?.postDelayed({
//                linearLayoutManager.smoothScrollToPositionWithDelay(pos,this@ImageScrollingActivity)
//                pos+=1
//                Log.e("handler","pos = $pos")
//            },100)
//
//            if(pos ==10){
//                doHandler?.removeCallbacksAndMessages(null)
//            }
//            doHandler?.post(this)
//        }
//        thread.start()


    }

    class Example3EventsAdapter(val onClick: (String) -> Unit) : RecyclerView.Adapter<Example3EventsAdapter.EventsViewHolder>() {

        val events = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
            return EventsViewHolder(
                EventItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )
        }

        override fun onBindViewHolder(viewHolder: EventsViewHolder, position: Int) {
            viewHolder.bind(events[position])
        }

        override fun getItemCount(): Int = events.size

        inner class EventsViewHolder(private val binding: EventItemViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {
                itemView.setOnClickListener {
                    onClick(events[adapterPosition])
                }
            }

            fun bind(title:String) {
                binding.itemEventText.text = title
            }
        }
    }
}
