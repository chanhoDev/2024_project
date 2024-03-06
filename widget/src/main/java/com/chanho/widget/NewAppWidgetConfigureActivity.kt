package com.chanho.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.chanho.widget.databinding.ActivityNewAppWidgetConfigureBinding

class NewAppWidgetConfigureActivity : AppCompatActivity() {

    companion object{
        const val APP_WIDGET_ID = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_app_widget_configure)

        var binding = ActivityNewAppWidgetConfigureBinding.inflate(layoutInflater)

        binding.widgetBtn.setOnClickListener {
            val appWidgetManager = AppWidgetManager.getInstance(this)
            updateAppWidget(this,appWidgetManager,APP_WIDGET_ID)

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, APP_WIDGET_ID)
            setResult(RESULT_OK,resultValue)
            finish()
        }
    }
}