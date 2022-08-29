package kr.ac.kpu.diyequipmentapplication.chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TransactionDateComparator implements Comparator<TransactionDTO> {
    @Override
    public int compare(TransactionDTO t1, TransactionDTO t2) {

        float date1, date2;

        date1 = Float.parseFloat(t1.gettLastTime());
        date2 = Float.parseFloat(t2.gettLastTime());

        if (date1 > date2) {
            return 1;
        }
        else if (date1 < date2) {
            return -1;
        }

        return 0;
    }
}
