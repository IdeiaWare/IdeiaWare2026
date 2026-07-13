package br.unisc.toolkit.classes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;

public class Data {
	 public static Timestamp horaAtual() {
	        Date date = new Date();
	        return new Timestamp(date.getTime());
	    }

	    public static String dataAtualFormatada() {
	        Date date = new Date();
	        return new SimpleDateFormat("dd/MM/yyyy").format(date);
	    }

	    public static String formatarData(Date dt) {
	        if (dt == null) {
	            dt = horaAtual();
	        }
	        return new SimpleDateFormat("dd/MM/yyyy").format(dt);
	    }

	    public static String formatarHora(Date dt) {
	        if (dt == null) {
	            dt = horaAtual();
	        }
	        return new SimpleDateFormat("HH:mm").format(dt);
	    }

	    public static String formatarDataHoraCompleta(Date dt) {
	        if (dt == null) {
	            dt = horaAtual();
	        }
	        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(dt);
	    }

	    public static String diferencaDatas(Date dt1, Date dt2) {
	        if (dt1 == null) {
	            dt1 = Data.horaAtual();
	        }
	        if (dt2 == null) {
	            dt2 = Data.horaAtual();
	        }

	        long diffInMillies = dt2.getTime() - dt1.getTime();
	        int dias = (int) TimeUnit.MILLISECONDS.toDays(diffInMillies);
	        int horas = (int) (TimeUnit.MILLISECONDS.toHours(diffInMillies) % 24);
	        int minutos = (int) TimeUnit.MILLISECONDS.toMinutes(diffInMillies) % 60;
	        
	        String msg;
	        
	        if (dias > 0) {
	             msg = dias + " dias, " + horas + " horas e " + minutos + " minutos";
	        } else if (horas > 0) {
	             msg = horas + " horas e " + minutos + " minutos";
	        } else {
	            msg = minutos + " minutos";
	        }
	        
	        msg += '.';
	        return msg;
	    }
}
