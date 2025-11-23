package com.example.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.example.project.R

class MainActivity : AppCompatActivity() {
    lateinit var lvList : ListView;
    lateinit var ivImg : ImageView;
    lateinit var tvResult : TextView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setControl()
        setEvent()


    }

    private fun setControl() {
        lvList = findViewById(R.id.lvList)
        ivImg = findViewById(R.id.ivImg)
        tvResult = findViewById(R.id.tvResult)
    }

    private fun setEvent() {
        val dsSanPham = mutableListOf<SanPham>();
        dsSanPham.add(SanPham("Item 1","airpod", R.drawable.airpod));
        dsSanPham.add(SanPham("Item 2", "laptop", R.drawable.laptop));
        dsSanPham.add(SanPham("Item 3", "mac", R.drawable.mac));

        val adapterSP = ArrayAdapter(this, android.R.layout.simple_list_item_1, dsSanPham)
        lvList.adapter = adapterSP
        val dsResult = mutableListOf<String>()

        lvList.setOnItemClickListener{ adapterView, view, i , l ->
            val sp = dsSanPham.get(i)
            ivImg.setImageResource(sp.img)
            dsResult.add("${dsResult.size + 1} ${sp.name}")
            tvResult.text = dsResult.joinToString("\n")
        }

        lvList.setOnItemLongClickListener { adapterView, view, i, l ->
            val sp = dsSanPham.get(i)
            dsResult.removeAt(i);
            adapterSP.notifyDataSetChanged()
            Toast.makeText(this, "Deleted ${sp.name}", Toast.LENGTH_SHORT).show()

            true
        }
    }
}
