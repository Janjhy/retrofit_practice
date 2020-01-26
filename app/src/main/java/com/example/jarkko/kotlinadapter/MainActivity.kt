package com.example.jarkko.kotlinadapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the adapter to convert the array to views
        val adapter = PresidentListAdapter(this, GlobalModel.presidents)

        // use a custom layout (instead of the ListActivity default layout)
        setContentView(R.layout.activity_main)

        // attach the adapter to a ListView
        mainlistview.adapter = adapter

        mainlistview.setOnItemClickListener { _, _, position, _ ->
            Log.d("USR", "Selected $position")
            selname.text = GlobalModel.presidents[position].toString()
            seldescription.text = GlobalModel.presidents[position].description
            Log.d("president name is", GlobalModel.presidents[position].name)
            callWebService(GlobalModel.presidents[position].name)
        }

        mainlistview.setOnItemLongClickListener { _, _, position, _ ->
            val selectedPresident = GlobalModel.presidents[position]
            val detailIntent = PresidentDetailActivity.newIntent(this, selectedPresident)

            startActivity(detailIntent)
            true
        }
    }

    fun callWebService(presidentString: String) {
        val call = TestApi.service.president( srsearch = presidentString)
        val value = object : Callback<TestApi.Model.Result> {
            override fun onResponse(call: Call<TestApi.Model.Result>, response:
            Response<TestApi.Model.Result>?) {
                if (response != null) {
                    var res: TestApi.Model.Result = response.body()!!
                    Log.d("DBG()", "${res.query.searchinfo.totalhits}") // just for the demo
                    selHits.text = ("Hits: " + res.query.searchinfo.totalhits.toString())
                }
            }
            override fun onFailure(call: Call<TestApi.Model.Result>, t: Throwable) {
                Log.e("DBG(failure)", t.toString())
            }
        }
        call.enqueue(value) // asynchronous request

    }

    private inner class PresidentListAdapter(context: Context, private val presidents: MutableList<President>) : BaseAdapter() {
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return presidents.size
        }

        override fun getItem(position: Int): Any {
            return presidents[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.item_president, parent, false)

            val thisPresident = presidents[position]
            var tv = rowView.findViewById(R.id.tvName) as TextView
            tv.text = thisPresident.name

            tv = rowView.findViewById(R.id.tvStartDuty) as TextView
            tv.text = Integer.toString(thisPresident.startDuty)

            tv = rowView.findViewById(R.id.tvEndDuty) as TextView
            tv.text = Integer.toString(thisPresident.endDuty)

            return rowView
        }
    }
}
