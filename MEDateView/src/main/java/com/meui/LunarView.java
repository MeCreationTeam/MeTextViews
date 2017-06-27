package com.meui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.*;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;
import android.content.*;

/**
 * Original Created by Hong.
 *
 * Copyright (C) 2017, Me Creation Team
 * Original GitHub Link:https://github.com/Hongarc/Lunar
 * Me Modify 2017-3-25
 * @author zhaozihanzzh
 */

public class LunarView extends TextView
{
	private final double DR=Math.PI/180;  // degree to radian
	private final String can[]= {"庚","辛","壬","癸","甲","乙","丙","丁","戊","己"};
	private final String chi[]={"申","酉","戌","亥","子","丑","寅","卯","辰","巳","午","未"};
	
    private static final int TIME_ZONE =8;//原来是7
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver(){
        public void onReceive(Context object, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)
                    || Intent.ACTION_TIME_CHANGED.equals(action)
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                    || Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                updateAmLich();
				action=null;
            }
        }
    };

    public LunarView(Context context) {
        super(context,null,0);
    }

    public LunarView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public LunarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //somethings
    }

    private void updateAmLich() {
        Calendar cal = Calendar.getInstance();
        int day   = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) +1;
        int year  = cal.get(Calendar.YEAR);
		cal=null;
        this.setText(ConvertAmLich(day,month,year));
    }
    private String ConvertAmLich (int day,int month,int year){
        final int lunarYear;
        final int dayNumber = converAmLich(day, month, year);
        int k = (int) ((dayNumber - 2415021.076998695) / 29.530588853);
        int dayStartMonth = getStartDay(k+1);

        //kiểm tra đã qua tháng chưa
        if(dayStartMonth >dayNumber){
            dayStartMonth=getStartDay(k);
        }
        int month11 = getLunarMonth11(year);
        int month11C =month11;
        if(month11>=dayStartMonth){
            lunarYear =year;
            month11 = getLunarMonth11(year-1);
        } else {
            lunarYear =year+1;
            month11C = getLunarMonth11(year+1);
        }
        final int lunarDay = dayNumber-dayStartMonth +1;
        final int diff = (dayStartMonth -month11)/29;
        int lunarMonth = diff + 11;
        if(month11C-month11 >365){
            final int cc =getLeapMonthOffset(month11);
            if ((diff>=cc)){
                lunarMonth = diff + 10;
            }
        }
        if (lunarMonth >12){
            lunarMonth-=12;
        }
        if ((lunarMonth>=11 && diff<4)){
            lunarYear-=1;
        }
		String lunarDayCN="";
		if(lunarDay<11)lunarDayCN="初"+getChineseNum(lunarDay,false);
		else
			if(lunarDay>10&lunarDay<20)lunarDayCN="十"+getChineseNum(lunarDay-10,false);
			else
				if(lunarDay==20)lunarDayCN="二十";
				else
					if(lunarDay>20&lunarDay<30)lunarDayCN="廿"+getChineseNum( lunarDay-20,false);
					else
						if(lunarDay==30)lunarDayCN="三十";
		return can[lunarYear%10]+chi[lunarYear%12]+"年"+"农历"+getChineseNum(lunarMonth,true)+"月"+lunarDayCN;
    }
	private String getChineseNum(final int num,boolean isMonth){
		switch(num){
			case 1:return isMonth? "正" : "一";
			case 2:return "二";
			case 3:return "三";
			case 4:return "四";
			case 5:return "五";
			case 6:return "六";
			case 7:return "七";
			case 8:return "八";
			case 9:return "九";
			case 10:return "十";
			case 11:return "十一";
			case 12:return isMonth? "腊" : "十二";
			default:return "";
		}
	}
   /* private String convYear(int yearLunar){
        return can[yearLunar%10]+chi[yearLunar%12]+"年";
    }*/
    
    private int getLeapMonthOffset(int a11) {
        final int k,i,last,arc;
        k = (int)((a11 - 2415021.076998695) / 29.530588853 + 0.5);
        i = 1; // We start with the month following lunar month 11
        arc = getSunLongitude(getStartDay(k+i));
        do {
            last = arc;
            i++;
            arc = getSunLongitude(getStartDay(k+i));
        } while (arc != last && i < 14);
        return i-1;
    }
    private int  converAmLich(int day,int month,int year) {
        int y, jd;
		final int m;
        y = -(14 - month) / 12;
        m = month - 12 * y - 3;
        y += year + 4800;
        jd = day + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083;
        if (jd >= 2299161) {
            jd += y / 400 - y / 100 + 38;
        }
        return jd;
    }
    private int getLunarMonth11(int yy){
        final int off = converAmLich(31, 12, yy) - 2415021;
        final int k = (int)(off / 29.530588853);
        int nm = getStartDay(k);
        final int sunLong = getSunLongitude(nm); // sun longitude at local midnight
        if (sunLong >= 9) {
            nm = getStartDay(k-1);
        }
        return nm;
    }
    private int getSunLongitude(int jdn) {
        final double T,T2,M,L0;
		double DL,L;
        T = (jdn - 2451545.5 - TIME_ZONE/24.0) / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        T2 = T*T;
        M = 357.52910 + 35999.05030*T - 0.0001559*T2 - 0.00000048*T*T2; // mean anomaly, degree
        L0 = 280.46645 + 36000.76983*T + 0.0003032*T2; // mean longitude, degree
        DL = (1.914600 - 0.004817*T - 0.000014*T2)*Math.sin(DR*M);
        DL = DL + (0.019993 - 0.000101*T)*Math.sin(DR*2*M) + 0.000290*Math.sin(DR*3*M);
        L = L0 + DL; // true longitude, degree
        L = L*DR;
        L = L - Math.PI*2*((int)(L/(Math.PI*2))); // Normalize to (0, 2*PI)
        return (int)(L / Math.PI * 6);
    }
    private int getStartDay(int k){
        final double Jd1,M,Mpr,F,C1,delta,JdNew;
        final double T =k/1236.85;
        final double T2 = T*T;
        final double T3 = T2*T;
        Jd1 = 2415020.75933 + 29.53058868*k + 0.0001178*T2 - 0.000000155*T3;
        Jd1 = Jd1 + 0.00033*Math.sin((166.56 + 132.87*T - 0.009173*T2)*DR); // Mean new moon
        M = 359.2242 + 29.10535608*k - 0.0000333*T2 - 0.00000347*T3; // Sun's mean anomaly
        Mpr = 306.0253 + 385.81691806*k + 0.0107306*T2 + 0.00001236*T3; // Moon's mean anomaly
        F = 21.2964 + 390.67050646*k - 0.0016528*T2 - 0.00000239*T3; // Moon's argument of latitude
        C1=(0.1734 - 0.000393*T)*Math.sin(M*DR) + 0.0021*Math.sin(2*DR*M);
        C1 = C1 - 0.4068*Math.sin(Mpr*DR) + 0.0161*Math.sin(DR*2*Mpr);
        C1 = C1 - 0.0004*Math.sin(DR*3*Mpr);
        C1 = C1 + 0.0104*Math.sin(DR*2*F) - 0.0051*Math.sin(DR*(M+Mpr));
        C1 = C1 - 0.0074*Math.sin(DR*(M-Mpr)) + 0.0004*Math.sin(DR*(2*F+M));
        C1 = C1 - 0.0004*Math.sin(DR*(2*F-M)) - 0.0006*Math.sin(DR*(2*F+Mpr));
        C1 = C1 + 0.0010*Math.sin(DR*(2*F-Mpr)) + 0.0005*Math.sin(DR*(2*Mpr+M));
        if (T < -11) {
            delta= 0.001 + 0.000839*T + 0.0002261*T2 - 0.00000845*T3 - 0.000000081*T*T3;
        } else {
            delta= -0.000278 + 0.000265*T + 0.000262*T2;
        }
        JdNew = Jd1 + C1 - delta;
        return (int)(JdNew + 0.5 + TIME_ZONE/24.0);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
		final ContentResolver cp=getContext().getContentResolver(); 
		//Can't use getApplicationContext! Cause NullPointerException and BOOTLOOP!
		if(Settings.System.getInt(cp,"ls_lunar",0)==1)
		{
			this.setVisibility(VISIBLE);
			this.setTextColor(Settings.System.getInt(cp,"ls_lunar_color",0xffffffff));
			this.setTextSize(Settings.System.getFloat(cp,"ls_lunar_size",18));
		}
		else
			this.setVisibility(INVISIBLE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);
        updateAmLich();
		
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mIntentReceiver);
    }
}
