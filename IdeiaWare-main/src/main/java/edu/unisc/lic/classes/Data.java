package edu.unisc.lic.classes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Data {

    // INFRA-TZ: formatacao sempre em America/Sao_Paulo, independente do TZ default da JVM
    // (fora do Docker, que ja fixa TZ=America/Sao_Paulo via env, o default variava com o host).
    private static final TimeZone FUSO = TimeZone.getTimeZone("America/Sao_Paulo");

    public Data() {
    }

    public static Timestamp horaAtual() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    private static SimpleDateFormat formato(String padrao) {
        SimpleDateFormat sdf = new SimpleDateFormat(padrao);
        sdf.setTimeZone(FUSO);
        return sdf;
    }

    public static String dataAtualFormatada() {
        return formato("dd/MM/yyyy").format(new Date());
    }

    // INFRA-09: null nao vira mais "hoje" (mostrava uma data que nunca aconteceu) -- vira vazio.
    public static String formatarData(Date dt) {
        if (dt == null) {
            return "";
        }
        return formato("dd/MM/yyyy").format(dt);
    }

    public static String formatarHora(Date dt) {
        if (dt == null) {
            return "";
        }
        return formato("HH:mm").format(dt);
    }

    public static String formatarDataHoraCompleta(Date dt) {
        if (dt == null) {
            return "";
        }
        return formato("dd/MM/yyyy HH:mm").format(dt);
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
