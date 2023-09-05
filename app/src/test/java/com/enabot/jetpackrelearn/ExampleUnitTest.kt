package com.enabot.jetpackrelearn

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 * 1993.11.11
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test01() {
        val localDate = LocalDate.of(2021, 5, 6)
        println("localdata=$localDate")
        val now = LocalDate.now()
        println("now=$now")
        println("${now.year}-${now.month.value}-${now.dayOfMonth}  ${now.dayOfWeek.value}")
    }

    @Test
    fun test2() {
        //1、得到指定时间
        val time: LocalTime = LocalTime.of(5, 26, 33, 2323);
        println("指定时间：$time");

        //2、获取当前的时间
        val now: LocalTime = LocalTime.now();
        println("当前时间：$now");

        //3、获取时间信息
        println("时" + now.hour);
        println("分" + now.minute);
        println("秒" + now.second);
        println("纳秒" + now.nano);
    }

    @Test
    fun test03() {
        //获取指定日期时间
        val dateTime: LocalDateTime = LocalDateTime.of(2023, 1, 1, 12, 12, 33, 1213)
        println("指定日期时间：$dateTime")

        //获取当前的日期时间
        val now: LocalDateTime = LocalDateTime.now()
        println("当前日期时间：$now")

        //获取日期时间信息
        println("年：" + now.year)
        println("月：" + now.month.value)
        println("日：" + now.dayOfMonth)
        println("星期：" + now.dayOfWeek.value)
        println("时" + now.hour)
        println("分" + now.minute)
        println("秒" + now.second)
        println("纳秒" + now.nano)
    }

    @Test
    fun test04() {
        val now = LocalDateTime.now()
        println("now = $now")

        //修改日期时间，每次修改都会重新创建一个新的对象
        val localDateTime = now.withYear(1998)
        println("当前时间：$now")
        println("修改年：$localDateTime")
        println("修改月：" + now.withMonth(8))
        println("修改天：" + now.withDayOfMonth(3))
        println("修改时：" + now.withHour(8))
        println("修改分：" + now.withMinute(15))
    }

    @Test
    fun test05() {
        val now = LocalDateTime.now()
        println("now = $now")

        //加减法
        println("两天后：" + now.plusDays(2))
        println("两年后：" + now.plusYears(2))
        println("六月后：" + now.plusMonths(6))
        println("三小时后：" + now.plusHours(3))
        println("十年前：" + now.minusYears(10))
        println("半年前：" + now.minusMonths(6))
        println("一周前：" + now.minusDays(7))
    }

    /**
     * 比较
     */
    @Test
    fun test06() {
        val date = LocalDate.of(2022, 1, 3)
        val now = LocalDate.now()
        println(now.isAfter(date))
        println(now.isBefore(date))
        println(now.isEqual(date))
    }

    /**
     * DateTimeFormatter日期时间格式化
     */
    @Test
    fun test07() {
        val now = LocalDateTime.now()
        //DateTimeFormatter.format将日期类型的对象转换为日期字符串（使用系统默认格式）
        val isoLocalDateTime: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val format = now.format(isoLocalDateTime)
        println(format)
        //DateTimeFormatter.format将日期类型的对象转换为日期字符串（使用指定格式）
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val format1 = now.format(formatter)
        println(format1)

        //DateTimeFormatter.parse将日期字符串转换为日期类型java对象（使用默认格式）
        val parse2 =
            LocalDateTime.parse("2023-02-18T17:25:48.267", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        println(parse2)
        //DateTimeFormatter.parse将日期字符串转换为日期类型java对象（使用指定格式）
        val parse = LocalDateTime.parse("1995-04-05 22:33:22", formatter)
        println(parse)
    }

    @Test
    fun test08() {
        //计算时间差
        val now = LocalTime.now()
        val time = LocalTime.of(22, 48, 59)

        //通过Duration来计算时间差
        val duration: Duration = Duration.between(now, time)
        println(now.toString() + "和" + time + "相差了（单位：天）" + duration.toDays())
        println(now.toString() + "和" + time + "相差了（单位：时）" + duration.toHours()) //取整时，舍去分之后的
        println(now.toString() + "和" + time + "相差了（单位：分）" + duration.toMinutes()) //取整分，舍去秒之后的
        println(now.toString() + "和" + time + "相差了（单位：毫秒）" + duration.toMillis()) //同理
    }

    @Test
    fun test09() {
        val nowDate = LocalDate.now()
        val date = LocalDate.of(1997, 12, 5)
        val period: Period = Period.between(date, nowDate)
        println(date.toString() + "和" + nowDate + "相差了（单位：年）：" + period.years) //只算了年份的加减，向下取整
        println(date.toString() + "和" + nowDate + "相差了（单位：月）：" + period.months) //只算了月份的加减
        println(date.toString() + "和" + nowDate + "相差了（单位：日）：" + period.days) //只算了天份的加减
        println(date.toString() + "和" + nowDate + "相差了：" + period.years + "年" + period.months + "个月" + period.days + "天")
    }

    /**
     * 时区处理
     */
    @Test
    fun test11() {
        //获取所有的时区
//        ZoneId.getAvailableZoneIds().forEach(System.out::println);

        //获取当前时间  中国使用的是东八区时区，比标准时间早8个小时
        val now = LocalDateTime.now()
        println(now)
        //获取标准时间
        val now1: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
        println(now1)
        //不指定时区时，默认使用计算机系统的时区
        val now2 = ZonedDateTime.now()
        println(now2)
        //使用指定时区创建日期时间
        val now3 = ZonedDateTime.now(ZoneId.of("America/Marigot"))
        println(now3)
    }

    /**
     * 标准时间转换为中国时间（东八区）
     */
    @Test
    fun test12() {
        val zdt = ZonedDateTime.parse("2019-07-31T16:00:00.000Z")
        val localDateTime = zdt.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val cst = formatter.format(localDateTime.plusHours(8))
        println("北京时间：$cst")
    }


}