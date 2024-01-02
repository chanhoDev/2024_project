package com.chanho.alarm

interface OptionModel {
    val content: String
    var isClick: Boolean
}

data class AmpmOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class HourOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class MinuteOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class AmpmAlldayOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class StartYearOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class StartMonthOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class StartDayOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class EndYearOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class EndMonthOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class EndDayOptionModel(
    override val content: String,
    override var isClick: Boolean
) : OptionModel

data class FortuneBirthTimeOptionModel(
    override val content: String,
    override var isClick: Boolean = false
) : OptionModel

data class BirthYearOptionModel(
    override val content: String,
    override var isClick: Boolean = false
) : OptionModel

data class BirthMonthOptionModel(
    override val content: String,
    override var isClick: Boolean = false
) : OptionModel

data class BirthDayOptionModel(
    override val content: String,
    override var isClick: Boolean = false
) : OptionModel

