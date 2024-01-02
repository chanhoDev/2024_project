package com.chanho.common

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

object Constants {
    const val WAPLAT_PACKAGE_NAME = "com.nhn.waplat"

    const val PURCHASE_USER_RESULT = "purchaseUserResult"
    const val IS_FIRST_PURCHASE_PRODUCT = "isFirstPurchaseProduct"

    const val PACKAGE_ID = "packageId"
    const val EMOCOG_SEQ = "emocogSeq"

    const val PURCHASED_WAPLAT_PACKAGE = "purchasedWaplatPackage"
    const val PURCHASED_PACKAGE_ID = "purchasedPackageId"
    const val PURCHASED_PACKAGE_SEQ = "purchasedPackageSeq"
    const val PRESENT_PURCHASER = "presentPurchaser"

    const val REQUEST_ALARM_TIME = "request_alarm_time"
    const val DATA = "data"
    const val CONTENT = "content"
    const val TIME = "time"
    const val ALARM_TYPE = "alarm_type"
    const val IS_ALARM_FIRST = "is_alarm_first"
    const val CHANNEL_ID = "channelId"
    const val ALARM_TIME = "alarm_time"
    const val ALARM_CODE = "alarm_code"

    const val IS_FIRST_SERVICE_VISIT = "isFirstServiceVisit"
    const val SCHEDULE_DETAIL_MODEL = "scheduleDetailModel"
    const val YEAR_START = 2000
    const val YEAR_END = 2100
    const val FORTUNE_SERVICE_TYPE = "fortuneServiceType"
    const val FRAGMENT_RESULT = "fragment_result"
    const val WEBVIEW_URL = "webview_url"
    const val WEBVIEW_TITLE = "webview_title"
    const val SCHEDULE_SEQ_ID = "schedule_seq_id"
    const val SCHEDULE_CURDATE = "schedule_curdate"
    const val SCHEDULE_REGISTRATION = "schedule_registration"
    const val REPORT_DETAIL_URL = "report_detail_url"
    const val REPORT_DETAIL_TITLE = "report_detail_title"

    const val SCHEDULE_SEQ = "schedule_seq"
    const val NOTICE_DETAIL_SEQ = "notice_detail_seq"
    const val MEDICATION_TYPE = "medication_type"
    const val SCHEDULE_TYPE = "schedule_type"
    const val REPORT_DETAIL_VIEW_TYPE = "report_detail_view_type"


    const val POPUP_PERMISSION_IN_MEDICATION = "popup_permission_in_medication"
    const val POPUP_PERMISSION_IN_SCHEDULE = "popup_permission_in_schedule"



    const val EVENT_CHANNEL = "event_channel"
    const val SERVICE_CHANNEL = "service_channel"

    const val NEVER_SHOW_POPUP = "never_show_popup"

    object AUTH {
        //for alpha server
        const val CLIENT_ID_WAPLAT_ALPHA = "bfs4jzVHRwhAvG0dwP2R"
        const val CLIENT_ID_DUELGO_ALPHA = "nqHY1X2AEOnWiXV8hhRl"
        const val SERVER_FE_BASE_URL_ALPHA = "http://45.125.232.81:8080"
        const val SERVER_GW_BASE_URL_ALPHA = "http://45.125.232.81:10080"

        //for real server
        const val CLIENT_ID_WAPLAT_REAL = "KNO10MJirTPjFzQTbWlQ"
        const val CLIENT_ID_DUELGO_REAL = "K52MdPuZ_pDjnuSc3iLf"
        const val SERVER_FE_BASE_URL_REAL = "http://180.210.64.143:8080"
        const val SERVER_GW_BASE_URL_REAL = "https://gateway.waplat.com"

        const val KEY_AUTH_RESULT = "waplat_auth_result"
    }


    enum class AlarmPopupType(val typeName: String) {
        MEDICATION("medication"),
        SCHEDULE("schedule");
        companion object {
            fun getAlarmPopupType(typeName: String?): AlarmPopupType {
                return AlarmPopupType.values().find {
                    it.typeName == typeName
                } ?: MEDICATION
            }
        }
    }

    enum class ReportDetailViewType(val typeName: String){
        REPORT_URL("reportUrl"),
        REPORT_PARAM("reportParam");

        fun getNameByType(context: Context, ampmAlldayType: AmpmAlldayType): String {
            return context.getString(ampmAlldayType.typeName)
        }
    }

    enum class ReportCategory(val categoryName:String) {
        WARAK("WARAK"), CEHCK("CHECK")
    }


    enum class ScheduleCategory(val categoryName: String) {
        BIRTHDAY("생일"),
        FOURTH("제사"),
        ANNIVERSARY("기념일"),
        FAMILY("가족"),
        FRIEND("친구"),
        MEETING("모임"),
        CUSTOM("직접 입력");
    }

    enum class AmpmAlldayType(@StringRes val typeName: Int) {
        ALL_DAY(R.string.common_allday),
        AM(R.string.common_am),
        PM(R.string.common_pm);

        companion object {
            fun getNameByType(context: Context, ampmAlldayType: AmpmAlldayType): String {
                return context.getString(ampmAlldayType.typeName)
            }
        }
    }



    enum class PopupType {
        WA_NOT_EXIST,
        FIRST_VISIT,
        NEW_SERVICE,
        BEFORE_EXPIRED,
        EXPIRED
    }



    enum class ReportType(val stringName: String) {
        CARE("CARE"),
        COGNITIVE_ABILITY("COGNITIVE_ABILITY"),
        WARAK("WARAK");

        companion object {
            fun getReportType(type: String?): ReportType {
                return ReportType.values()
                    .find { it.stringName == type } ?: WARAK

            }
        }
    }



    enum class WaplatMainHomeTab {
        WAPLAT_HOME,
        WAPLAT_REPORT,
        WAPLAT_FRONT_DESK,
        WAPLAT_SERVICE
    }

    enum class WaplatReportType(val path:String) {
        CARE("CARE"),
        WARAK("WARAK"),
        COGNITIVE_ABILITY("COGNITIVE_ABILITY"),
        BLOOD_PRESSURE("BLOOD_PRESSURE")
    }


    enum class WaplatDeepLinkPath(val path: String) {
        PRODUCT_SERVICE_DETAIL_INFO("product_service_detail_info"),
        PRODUCT_SERVICE("product_service"),
        EMOCOG_CHECK("emocog_check"),
        EMOCOG_CHECK_DETAIL("emocog_check_detail"),
        HOME("home"),
        REPORT("report"),
        REPORT_DETAIL("report_detail"),
        FRONTDESK("frontdesk");

        companion object {
            fun getDeeplinkPath(path: String): WaplatDeepLinkPath {
                return values().find { it.path == path } ?: PRODUCT_SERVICE
            }
        }
    }

    enum class WaplatServiceIds(
        val packageId: String,
        val categoryId: String,
        val packageName: String
    ) {
        COGNITY_CHECK(packageId = "ck000002", categoryId = "ck000000", packageName = "기억검사"),
        PULSE_CHECK(packageId = "ck000003", categoryId = "ck000000", packageName = "심혈관체크"),
        WARAK(packageId = "wa000001", categoryId = "wa000000", packageName = "와락(樂)"),
        WARAK_RADIO(packageId = "wa000002", categoryId = "wa000000", packageName = "와락(樂) 라디오");

        companion object {
            fun getServiceByPackageId(packageId: String): WaplatServiceIds {
                return values().find { it.packageId == packageId } ?: WARAK
            }
        }
    }


    enum class EarthlyBranches(val s: String) {
        MOUSE("자"),
        HORSE("오"),
        PIG("해"),
        CHICKEN("유"),
        SHEEP("미"),
        TIGER("인"),
        SNAKE("사"),
        DOG("술"),
        RABBIT("묘"),
        DRAGON("진"),
        COW("축"),
        MONKEY("신");

        companion object {
            fun getName(s: String): EarthlyBranches {
                return values().find { it.s == s } ?: HORSE
            }
        }
    }

    enum class CalendarType(val s: String) {
        YANG_RYUK("양력"),
        PYUNG_DAL("음력/평달"),
        YUN_DAL("음력/윤달");

        companion object {
            fun getName(s: String): CalendarType {
                return values().find { it.name == s } ?: YANG_RYUK
            }
        }
    }

    enum class GenderType(val s: String) {
        MALE("MALE"),
        FEMALE("FEMALE");

        companion object {
            fun getName(s: String): GenderType {
                return values().find { it.name == s } ?: MALE
            }
        }
    }

    enum class FortuneServiceType(val s: String) {
        TODAY("TODAY"),
        NEWYEAR("NEWYEAR"),
        SAJU("SAJU");

        companion object {
            fun getName(s: String): FortuneServiceType {
                return values().find { it.name == s } ?: TODAY
            }
        }
    }

    enum class StatusName(val s: String) {
        New("new"),
        Open("open"),
        Reply("reply"),
        Solved("solved"),
        Closed("closed");

        companion object {
            fun getStatusName(s: String): StatusName {
                return values().find { it.s == s } ?: New
            }
        }
    }

    const val NOTICE_SEQ = "noticeSeq"

    enum class NoticeType(val s: String) {
        NOTICE("NOTICE"),
        EVENT("EVENT");

        companion object {
            fun getNoticeTypeName(s: String): NoticeType {
                return NoticeType.values().find { it.name == s } ?: NOTICE
            }
        }
    }

    object HelpServiceCode {
        const val FORTUNE = "s000002"
        const val SCHEDULE = "s000003"
        const val MEDICATION = "s000004"
        const val RADIO = "s000006"
        const val EMOCOG = "s000007"
    }

    object RADIO {
        const val ACTION_SET_RADIO_CHANNEL_ITEM = "action_set_radio_channel_item"
        const val KEY_EXTRA_SET_RADIO_CHANNEL_ITEM = "key_extra_set_radio_channel_item"
        const val KEY_EXTRA_SET_RADIO_STREAMING_INFO = "key_extra_set_radio_streaming_info"

        const val ACTION_SET_RADIO_TRACK_NEXT = "action_set_radio_track_next"

        const val ACTION_STOP_MEDIA_SESSION_SERVICE = "action_stop_media_session_service"
    }
}