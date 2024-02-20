package com.chanho.motion

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar


class GyroScopeMotionService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor

    //roll and pitch
    var timeStamp: Double = 0.0
    var dt: Double = 0.0
    val NS2S = 1.0f / 1000000000.0f
    var RAD2DGR = 180 / Math.PI
    var pitch: Double = 0.0 // y 축 방향의 각도
    var roll: Double = 0.0 //x축
    var yaw: Double = 0.0// z축

    override fun onCreate() {
        super.onCreate()
        Log.e("GyroScopeMotionService","start")
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) as Sensor
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI)
        return START_NOT_STICKY

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let { event ->
            //각 축의 각속도 성분을 받는다
            var gyroX = event.values[0]
            var gyroY = event.values[1]
            var gyroZ = event.values[2]
            //각속도를 적분하여 회전각을 추출하기 위해 적분 간격dt를 구한다.
            //dt : 센서가 현재 상태를 감지하는 시간 간격
            //NS2S: Nano second -> second
            dt = (event.timestamp - timeStamp) * NS2S
            timeStamp = event.timestamp.toDouble()

            //맨 센서 인식을 활성화하여 처음 timeStamp가 0일때는 dt 값이 올바르지 않으므로 넘어간다
            if (dt - timeStamp * NS2S != 0.0) {
                //각속도 성분을 적분 -> 회전각(pitch,roll)으로 변환
                //여기까지의 pitch, roll의 단위는 라디안이다
                //그래서 아래 로그 출력부분에서 멤버변수 RAD2DGR를 곱해주어 degree로 변환해줌
                pitch += gyroY * dt
                roll += gyroX * dt
                yaw += gyroZ * dt

                Log.e(
                    "LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                            + "           [Y]:" + String.format("%.4f", event.values[1])
                            + "           [Z]:" + String.format("%.4f", event.values[2])
                            + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                            + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                            + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                            + "           [dt]: " + String.format("%.4f", dt)
                )
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        var setString:Set<String> = PrefHelper[GYROSCOPE, setOf<String>()]
        val cal = Calendar.getInstance()
        val time = Util.dateFormat.format(cal.time)
        val resultPitch = String.format("%.1f", pitch * RAD2DGR)
        val resultRoll = String.format("%.1f", roll * RAD2DGR)
        val resultYaw = String.format("%.1f", yaw * RAD2DGR)
        setString = setString.plus("[time=$time,pitch=$resultPitch//roll=$resultRoll//yaw=$resultYaw]")
        PrefHelper[GYROSCOPE] = setString
        sensorManager.unregisterListener(this)
        Log.e("GyroScopeMotionService","finish ${PrefHelper[GYROSCOPE, setOf<String>()]}")
    }

    companion object{
        val GYROSCOPE = "gyroscope5"
    }

}

