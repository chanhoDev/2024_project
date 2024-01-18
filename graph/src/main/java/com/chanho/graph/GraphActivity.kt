package com.chanho.graph

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.chanho.graph.databinding.ActivityGraphBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GraphActivity : AppCompatActivity(),OnChartGestureListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGraphBinding
    lateinit var chart:BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            lineChart.apply {
                setPinchZoom(false)
                setScaleEnabled(false)
                isDoubleTapToZoomEnabled = false
                // right, left, x axis disabled.
                // legend, description disabled.
                axisRight.isEnabled = false
                axisLeft.isEnabled = false
                xAxis.isEnabled = false
                legend.isEnabled = false
                description.isEnabled = false

                val dataList = arrayListOf(
                    70800.0, 70900.0, 71000.0, 71200.0, 71200.0, 70900.0,
                    71000.0, 70900.0, 71100.0, 71000.0, 71200.0, 71300.0,
                    71500.0, 71600.0, 71200.0, 70900.0, 70800.0, 70700.0,
                    70800.0, 71000.0
                )

                val entryList = arrayListOf<Entry>()
                dataList.forEachIndexed { index, d ->
                    entryList.add(Entry(index.toFloat(), d.toFloat()))
                }
                val lineDataSet = LineDataSet(entryList, "data").apply {
                    // 원 크기 설정 - 선이 끊어지지 않는 것처럼 보이기 위해 선 두께에 맞춰서 설정
                    circleRadius = 1.0F
                    circleHoleRadius = 1.0F
                    // 원 색상 설정
                    setCircleColor(ContextCompat.getColor(this@GraphActivity,com.chanho.graph.R.color.secondary_purple_900 ))
                    setCircleColorHole(ContextCompat.getColor(this@GraphActivity, com.chanho.graph.R.color.secondary_purple_900))
                    // 각 지점의 데이터 텍스트 크기
                    valueTextSize = 11.0F
                    // 텍스트 색상
                    valueTextColor = ContextCompat.getColor(this@GraphActivity, com.chanho.graph.R.color.secondary_purple_900)
                    // 선 두께
                    lineWidth = 2.0F
                    // 선 색상
                    color = ContextCompat.getColor(this@GraphActivity, com.chanho.graph.R.color.secondary_purple_900)
                }
                data = LineData(listOf(lineDataSet))
                invalidate()
            }
            pieChart.apply {
                getDescription().setEnabled(false)

//                val tf = Typeface.createFromAsset(context.assets, "OpenSans-Light.ttf")

//                setCenterTextTypeface(tf)
                setCenterText(generateCenterText())
                setCenterTextSize(10f)
//                setCenterTextTypeface(tf)

                // radius of the center hole in percent of maximum radius

                // radius of the center hole in percent of maximum radius
                setHoleRadius(45f)
                setTransparentCircleRadius(50f)

                val l: Legend = getLegend()
                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                l.orientation = Legend.LegendOrientation.VERTICAL
                l.setDrawInside(false)

                setData(generatePieData())
            }


            chart = BarChart(this@GraphActivity)
            chart.getDescription().setEnabled(false)
            chart.setOnChartGestureListener(this@GraphActivity)

            val mv = MyMarkerView(this@GraphActivity, com.chanho.graph.R.layout.custom_marker_view)
            mv.setChartView(chart) // For bounds control

            chart.setMarker(mv)

            chart.setDrawGridBackground(false)
            chart.setDrawBarShadow(false)

//            val tf = Typeface.createFromAsset(this@GraphActivity.getAssets(), "OpenSans-Light.ttf")

            chart.setData(generateBarData(1, 20000f, 12))

            val l: Legend = chart.getLegend()
//            l.typeface = tf

            val leftAxis: YAxis = chart.getAxisLeft()
//            leftAxis.typeface = tf
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            chart.getAxisRight().setEnabled(false)

            val xAxis: XAxis = chart.getXAxis()
            xAxis.isEnabled = false

            // programmatically add the chart

            // programmatically add the chart
            val parent: FrameLayout = binding.parentLayout
            parent.addView(chart)

        }


    }
    private val mLabels =
        arrayOf("Company A", "Company B", "Company C", "Company D", "Company E", "Company F")
//    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };

    //    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };
    private fun getLabel(i: Int): String? {
        return mLabels[i]
    }
    protected fun generateBarData(dataSets: Int, range: Float, count: Int): BarData? {
        val sets = java.util.ArrayList<IBarDataSet>()
        for (i in 0 until dataSets) {
            val entries = java.util.ArrayList<BarEntry>()

//            entries = FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "stacked_bars.txt");
            for (j in 0 until count) {
                entries.add(BarEntry(j.toFloat(), (Math.random() * range).toFloat() + range / 4))
            }
            val ds = BarDataSet(entries, getLabel(i))
            ds.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            sets.add(ds)
        }
        val d = BarData(sets)
//        d.setValueTypeface(tf)
        return d
    }

    private fun generateCenterText(): SpannableString? {
        val s = SpannableString("Revenues\nQuarters 2015")
        s.setSpan(RelativeSizeSpan(2f), 0, 8, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 8, s.length, 0)
        return s
    }
    protected fun generatePieData(): PieData? {
        val count = 4
        val entries1 = ArrayList<PieEntry>()
        for (i in 0 until count) {
            entries1.add(PieEntry((Math.random() * 60 + 40).toFloat(), "Quarter " + (i + 1)))
        }
        val ds1 = PieDataSet(entries1, "Quarterly Revenues 2015")
        ds1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        ds1.sliceSpace = 2f
        ds1.valueTextColor = Color.WHITE
        ds1.valueTextSize = 12f
        val d = PieData(ds1)
        return d
    }


    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartGesture?) {
        Log.i("Gesture", "START")
    }

    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartGesture?) {
        Log.i("Gesture", "END")
        chart.highlightValues(null)
    }

    override fun onChartLongPressed(me: MotionEvent?) {
        Log.i("LongPress", "Chart long pressed.")
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {
        Log.i("DoubleTap", "Chart double-tapped.")
    }

    override fun onChartSingleTapped(me: MotionEvent?) {
        Log.i("SingleTap", "Chart single-tapped.")
    }

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
        Log.i("Fling", "Chart fling. VelocityX: $velocityX, VelocityY: $velocityY")
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        Log.i("Scale / Zoom", "ScaleX: $scaleX, ScaleY: $scaleY")
    }

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        Log.i("Translate / Move", "dX: $dX, dY: $dY")
    }
}

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
class MyMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView

    init {
        tvContent = findViewById<View>(com.chanho.graph.R.id.tvContent) as TextView
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if (e is CandleEntry) {
            tvContent.text =
                "" + Utils.formatNumber(e.high, 0, true)
        } else {
            tvContent.text = "" + Utils.formatNumber(e.y, 0, true)
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}