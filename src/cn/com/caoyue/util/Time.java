// * * * * * * * * * * * * * * * * * * * * * * * *
// * REDROCK-TEAM HOMEWORK 2 (20151011)          *
// * Level 4 - Make a Class for Time Convert     *
// * Author:  Haruue Icymoon                     *
// * Time:    Thu Oct 15 21:23:57 CST 2015       *
// * Website: http://www.caoyue.com.cn/          *
// * * * * * * * * * * * * * * * * * * * * * * * *

package cn.com.caoyue.util;
import com.ant.jobgod.jobgod.util.ITime;

public class Time implements ITime {
    private int year = 1970, month = 1, day = 1, hour = 0, minute = 0, second = 0, timeZone = 8, microSecond = 0;
    private long timeStamp;

    //define Constructors
    public Time() {

    }

    public Time(Time origTime) {
        timeStamp = origTime.getTimeStamp();
        stampToHuman();
    }

    public Time(long timeStamp) {
        set(timeStamp);
    }

    public Time(long timeStamp, int timeZone) {
        this.timeZone = timeZone;
        set(timeStamp);
    }

    public Time(long timeStamp, String timeZone) {
        setTimeZone(timeZone);
        set(timeStamp);
    }

    public Time(int year, int month, int day, int hour, int minute, int second) {
        set(year, month, day, hour, minute, second);
    }

    public Time(int year, int month, int day, int hour, int minute, int second, int timeZone) {
        this.timeZone = timeZone;
        set(year, month, day, hour, minute, second);
    }

    public Time(int year, int month, int day, int hour, int minute, int second, String timeZone) {
        setTimeZone(timeZone);
        set(year, month, day, hour, minute, second);
    }

    public Time(String time, String format) {
        parse(time, format);
    }

    public Time(int day, int hour, int minute, int second) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        humanToStamp();
    }

    //necessary math function
    private static boolean isLeapYear(int year) {
        return ((((year % 100) == 0) && (year % 400 == 0)) || (((year % 100) != 0) && ((year % 4) == 0)));
    }

    private static int daysOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if (isLeapYear(year)) return 29;
            else return 28;
        }
        return 0; //ERROR month
    }

    private static int daysOfYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    private static long secondsOfMonth(int year, int month) {
        return 86400 * (long) daysOfMonth(year, month);
    }

    public static long secondsOfYear(int year) {
        return isLeapYear(year) ? 31622400 : 31536000;
    }

    //stamp --> human
    private void stampToHuman() {
        //init timeHuman
        year = 1970;
        month = 1;
        day = 1;
        hour = 0;
        minute = 0;
        second = 0;
        timeZone = 8;
        microSecond = 0;
        //add timezone
        long timeStamp = this.timeStamp + (long) (3600 * timeZone);
        //when time stamp is negative
        if (timeStamp < 0) {
            negativeStampToHuman(timeStamp);
            return;
        }
        //get year
        while (timeStamp - secondsOfYear(year) >= 0) {
            timeStamp -= secondsOfYear(year);
            year++;
        }
        //get month
        while (timeStamp - secondsOfMonth(year, month) >= 0) {
            timeStamp -= secondsOfMonth(year, month);
            month++;
        }
        //get day
        day = 1 + (int) ((timeStamp) / 86400);
        timeStamp %= 86400;
        //get hour
        hour = (int) ((timeStamp) / 3600);
        timeStamp %= 3600;
        //get minute
        minute = (int) ((timeStamp) / 60);
        timeStamp %= 60;
        //get second
        second = (int) timeStamp;
    }

    //negative stamp --> human
    private void negativeStampToHuman(long timeStamp) {
        timeStamp = -timeStamp;
        //get time first
        second = (int) (((timeStamp % 60) == 0) ? 0 : (60 - (timeStamp % 60)));
        timeStamp -= timeStamp % 60;
        minute = (int) (((timeStamp % 3600) == 0) ? ((second != 0) ? 59 : 0) : (((second != 0) ? (59 - (timeStamp % 3600) / 60) : (60 - (timeStamp % 3600) / 60))));
        timeStamp -= timeStamp % 3600;
        hour = (int) (((timeStamp % 86400) == 0) ? ((minute != 0) ? 23 : 0) : (((minute != 0) ? (23 - (timeStamp % 86400) / 3600) : (24 - (timeStamp % 86400) / 3600))));
        timeStamp -= timeStamp % 86400;
        //get year and month
        year = 1969;
        month = 12;
        while (timeStamp >= secondsOfYear(year)) {
            timeStamp -= secondsOfYear(year);
            year--;
        }
        while (timeStamp >= secondsOfMonth(year, month)) {
            timeStamp -= secondsOfMonth(year, month);
            month--;
        }
        //get day
        day = daysOfMonth(year, month);
        day -= (int) (timeStamp / 86400);
    }

    //human --> stamp, year >= 1970
    private void humanToStamp() {
        if (year < 1970) {
            negativeHumanToStamp();
            return;
        }
        //init stamp
        long stamp = 0;
        //sum the seconds before the input year
        for (int year = 1970; year < this.year; year++) {
            stamp += secondsOfYear(year);
        }
        //sum the seconds in the input year and before the input month
        for (int month = 1; month < this.month; month++) {
            stamp += secondsOfMonth(this.year, month);
        }
        //sum the seconds in the input month and before the input day, day = 0 will be use in add() and minus()
        stamp += (day != 0) ? (86400 * (this.day - 1)) : 0;
        //sum the seconds in the input day
        stamp += 3600 * (this.hour);
        stamp += 60 * (this.minute);
        stamp += this.second;
        stamp -= (long) (this.timeZone * 3600);
        timeStamp = stamp;
    }

    //human --> stamp, year < 1970
    private void negativeHumanToStamp() {
        //init stamp
        long stamp = 0;
        for (int year = 1969; year > this.year; year--) {
            stamp += secondsOfYear(year);
        }
        for (int month = 12; month > this.month; month--) {
            stamp += secondsOfMonth(this.year, month);
        }
        stamp += 86400 * (daysOfMonth(this.year, this.month) - this.day);
        stamp += (3600 * (23 - hour));
        stamp += (60 * (59 - minute));
        stamp += (60 - second);
        stamp += (long) (this.timeZone * 3600);
        timeStamp = -stamp;
    }

    public boolean equals(Time subTime) {
        return timeStamp == subTime.getTimeStamp();
    }

    @Override
    public String toString() {
        return format("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void set(long timeStamp) {
        this.timeStamp = timeStamp;
        stampToHuman();
    }

    @Override
    public void set(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        humanToStamp();
    }

    @Override
    public void setYear(int year) {
        this.year = year;
        humanToStamp();
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setMonth(int month) {
        this.month = month;
        humanToStamp();
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public void setDay(int day) {
        this.day = day;
        humanToStamp();
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public void setHour(int hour) {
        this.hour = hour;
        humanToStamp();
    }

    @Override
    public int getHour() {
        return hour;
    }

    @Override
    public void setMinute(int minute) {
        this.minute = minute;
        humanToStamp();
    }

    @Override
    public int getMinute() {
        return minute;
    }

    @Override
    public void setSecond(int second) {
        this.second = second;
        humanToStamp();
    }

    @Override
    public int getSecond() {
        return second;
    }

    /**
     * ȡ���ڼ�
     *
     * @return
     */
    @Override
    public int getWeek() {
        if (month < 3) {
            month += 12;
            --year;
        }
        return (day + 1 + 2 * month + 3 * (month + 1) / 5 + year + (year >> 2) - year / 100 + year / 400) % 7;
    }

    /**
     * ȡ�����ǵ���ڼ���
     *
     * @return
     */
    @Override
    public int getWeekOfYear() {
        int firstMonday;
        for (firstMonday = 1; ; firstMonday++) {
            if (month < 3) {
                month += 12;
                --year;
            }
            if ((firstMonday + 1 + 2 * month + 3 * (month + 1) / 5 + year + (year >> 2) - year / 100 + year / 400) % 7 == 1) {
                break;
            }
        }
        if (getDayOfYear() < firstMonday) {
            int firstMondayOfLastYear;
            int year = this.year - 1;
            for (firstMondayOfLastYear = 1; ; firstMondayOfLastYear++) {
                if (month < 3) {
                    month += 12;
                    --year;
                }
                if ((firstMondayOfLastYear + 1 + 2 * month + 3 * (month + 1) / 5 + year + (year >> 2) - year / 100 + year / 400) % 7 == 1) {
                    break;
                }
                return (daysOfYear(year) - firstMondayOfLastYear + firstMonday + 1) / 7 + 1;
            }


        }
        return (getDayOfYear() - firstMonday + 1) / 7 + 1;

    }

    /**
     * ȡ�����ǵ���ڼ���
     *
     * @return
     */
    @Override
    public int getDayOfYear() {
        int result = 0;
        for (int month = 1; month < this.month; month++) {
            result += daysOfMonth(this.year, this.month);
        }
        result += this.day;
        return result;
    }

    /**
     * ȡʱ���
     *
     * @return
     */
    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * ʱ��Ӽ�
     *
     * @param data
     */
    @Override
    public void add(ITime data) {
        this.timeStamp += data.getDay() * 86400 + data.getHour() * 3600 + data.getMinute() * 60 + data.getSecond();
        stampToHuman();
    }

    @Override
    public void minus(ITime data) {
        this.timeStamp -= data.getTimeStamp();
        long timeStamp = this.timeStamp;
        second = (int) (timeStamp % 60);
        timeStamp -= second;
        minute = (int) ((timeStamp % 3600) / 60);
        timeStamp -= minute * 60;
        hour = (int) ((timeStamp % 86400) / 3600);
        timeStamp -= hour * 3600;
        day = (int) (timeStamp / 86400);
    }

    /**
     * ʱ���ʽ��������
     * ��������yyyy��MM��dd�� hh:mm:ss  �� 2015��10��14�� 08:00:00 ������ʱ��
     *
     * @param time   ʱ���ı�
     * @param format ��:y		��:M		��:d		ʱ:h(12��)/H(24ֵ)	��:m		��:s		����:S
     */
    @Override
    public void parse(String time, String format) {
        String[] timeArray = time.split("[^0-9]");
        String thisCase, lastCase = "";
        int index = 0;
        for (; timeArray[index].equals(""); index++) ;
        for (int i = 0; i < format.length(); i++) {
            if (index >= timeArray.length) {
                break;
            }
            for (; timeArray[index].isEmpty(); index++) ;
            thisCase = format.substring(i, i + 1);
            if (thisCase.matches("[yMdHhmsS]") && !thisCase.equals(lastCase)) {
                switch (thisCase) {
                    case "y":
                        year = Integer.parseInt(timeArray[index]);
                        break;
                    case "M":
                        month = Integer.parseInt(timeArray[index]);
                        break;
                    case "d":
                        day = Integer.parseInt(timeArray[index]);
                        break;
                    case "H":
                    case "h":
                        hour = Integer.parseInt(timeArray[index]);
                        break;
                    case "m":
                        minute = Integer.parseInt(timeArray[index]);
                        break;
                    case "s":
                        second = Integer.parseInt(timeArray[index]);
                        break;
                    case "S":
                        microSecond = Integer.parseInt(timeArray[index]);
                        break;
                }
                index++;
            }
            lastCase = thisCase;
        }
        humanToStamp();
    }

    /**
     * ʱ���ʽ����
     * ��������yyyy��MM��dd�� hh:mm:ss  �򷵻�2015��10��14�� 08:00:00
     *
     * @param format ��ʽ ��:y		��:M		��:d		ʱ:h(12��)/H(24ֵ)	��:m		��:s		����:S
     * @return ��ʽ��ʱ���ı�
     */
    @Override
    public String format(String format) {
        format = format.replaceAll("yyyy", Integer.toString(year));
        format = format.replaceAll("y", Integer.toString(year));
        format = format.replaceAll("MM", (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month));
        format = format.replaceAll("M", Integer.toString(month));
        format = format.replaceAll("dd", (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day));
        format = format.replaceAll("d", Integer.toString(day));
        format = format.replaceAll("HH", (hour < 10) ? "0" + Integer.toString(hour) : Integer.toString(hour));
        format = format.replaceAll("H", Integer.toString(hour));
        format = format.replaceAll("hh", (hour % 12 < 10) ? "0" + Integer.toString(hour % 12) : Integer.toString(hour % 12));
        format = format.replaceAll("h", Integer.toString(hour % 12));
        format = format.replaceAll("mm", (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute));
        format = format.replaceAll("m", Integer.toString(minute));
        format = format.replaceAll("ss", (second < 10) ? "0" + Integer.toString(second) : Integer.toString(second));
        format = format.replaceAll("s", Integer.toString(second));
        format = format.replaceAll("SS", (microSecond < 10) ? "0" + Integer.toString(microSecond) : Integer.toString(microSecond));
        format = format.replaceAll("S", Integer.toString(microSecond));
        return format;
    }

    /**
     * ʱ���л�
     *
     * @param timeZone ʱ�����֡�eg:GMT,UTC  ,CCT ,JST�ȡ���ʱ�����ת����Ĭ��CCT��
     */
    @Override
    public void setTimeZone(String timeZone) {
        switch (timeZone) {
            case "GMT":
            case "UTC":
                this.timeZone = 0;
                break;
            case "CCT":
                this.timeZone = 8;
                break;
            case "JST":
                this.timeZone = 9;
                break;
        }
        stampToHuman();
    }
}