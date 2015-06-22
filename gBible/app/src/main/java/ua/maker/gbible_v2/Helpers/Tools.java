package ua.maker.gbible_v2.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Models.BooksId;
import ua.maker.gbible_v2.R;
import ua.maker.gbible_v2.SettingsActivity;

/**
 * Created by daniil on 11/7/14.
 */
public class Tools {

    public static void initProgressBar(ProgressBar progressBar) {
        progressBar.getIndeterminateDrawable()
                .setColorFilter(GBApplication.getInstance().getResources()
                        .getColor(R.color.progress_load_color), PorterDuff.Mode.MULTIPLY);
    }

    public static String getBookNameByBookId(int bookId, Context context){
        String name = "";

        switch(bookId){
            case BooksId.BITIE_ID:
                name = context.getResources().getString(R.string.bitie_str);
                break;
            case BooksId.ISHOD_ID:
                name = context.getResources().getString(R.string.ishod_str);
                break;
            case BooksId.LEVIT_ID:
                name = context.getResources().getString(R.string.levit_str);
                break;
            case BooksId.NUMBER_ID:
                name = context.getResources().getString(R.string.number_str);
                break;
            case BooksId.VTOROZ_ID:
                name = context.getResources().getString(R.string.vtorozakonie_str);
                break;
            case BooksId.IISUS_NAVIN_ID:
                name = context.getResources().getString(R.string.iisus_navin_str);
                break;
            case BooksId.SUDII_ID:
                name = context.getResources().getString(R.string.sudii_str);
                break;
            case BooksId.RUTH_ID:
                name = context.getResources().getString(R.string.ruf_str);
                break;
            case BooksId.ONE_CARSTV_ID:
                name = context.getResources().getString(R.string.one_carstw_str);
                break;
            case BooksId.TWO_CARSTV_ID:
                name = context.getResources().getString(R.string.two_carstw_str);
                break;
            case BooksId.THREE_CARSTV_ID:
                name = context.getResources().getString(R.string.three_carstw_str);
                break;
            case BooksId.FOUR_CARSTV_ID:
                name = context.getResources().getString(R.string.four_carstw_str);
                break;
            case BooksId.ONE_PARALIPAM_ID:
                name = context.getResources().getString(R.string.one_paralipamenon_str);
                break;
            case BooksId.TWO_PARALIPAM_ID:
                name = context.getResources().getString(R.string.two_paralipamenon_str);
                break;
            case BooksId.EZDRA_ID:
                name = context.getResources().getString(R.string.ezdra_str);
                break;
            case BooksId.NEEMIA_ID:
                name = context.getResources().getString(R.string.neemia_str);
                break;
            case BooksId.ESFIR_ID:
                name = context.getResources().getString(R.string.esfir_str);
                break;
            case BooksId.IOV_ID:
                name = context.getResources().getString(R.string.iov_str);
                break;
            case BooksId.PSALTIR_ID:
                name = context.getResources().getString(R.string.psaltir_str);
                break;
            case BooksId.PRITCHI_ID:
                name = context.getResources().getString(R.string.pritchi_str);
                break;
            case BooksId.ECCL_ID:
                name = context.getResources().getString(R.string.ecclesiast_str);
                break;
            case BooksId.PESNI_PESNEY_ID:
                name = context.getResources().getString(R.string.pesni_pesney_str);
                break;
            case BooksId.ISAIA_ID:
                name = context.getResources().getString(R.string.isaia_str);
                break;
            case BooksId.IEREMIA_ID:
                name = context.getResources().getString(R.string.ieremia_str);
                break;
            case BooksId.PLACH_IEREM_ID:
                name = context.getResources().getString(R.string.plach_ieremii_str);
                break;
            case BooksId.IZEKIIL_ID:
                name = context.getResources().getString(R.string.iezekiil_str);
                break;
            case BooksId.DANIIL_ID:
                name = context.getResources().getString(R.string.daniil_str);
                break;
            case BooksId.OSIA_ID:
                name = context.getResources().getString(R.string.osia_str);
                break;
            case BooksId.IOIL_ID:
                name = context.getResources().getString(R.string.ioil_str);
                break;
            case BooksId.AMOS_ID:
                name = context.getResources().getString(R.string.amos_str);
                break;
            case BooksId.AVDIY_ID:
                name = context.getResources().getString(R.string.avdiy_str);
                break;
            case BooksId.IONA_ID:
                name = context.getResources().getString(R.string.iona_str);
                break;
            case BooksId.MICHEY_ID:
                name = context.getResources().getString(R.string.michey_str);
                break;
            case BooksId.NAUM_ID:
                name = context.getResources().getString(R.string.naum_str);
                break;
            case BooksId.AVVAKUM_ID:
                name = context.getResources().getString(R.string.avvakum_str);
                break;
            case BooksId.SOFONIA_ID:
                name = context.getResources().getString(R.string.sofonia_str);
                break;
            case BooksId.AGGEY_ID:
                name = context.getResources().getString(R.string.aggey_str);
                break;
            case BooksId.ZAHARIA_ID:
                name = context.getResources().getString(R.string.zaharia_str);
                break;
            case BooksId.MALAKHIA_ID:
                name = context.getResources().getString(R.string.malahia_str);
                break;
            case BooksId.MATFEY_ID:
                name = context.getResources().getString(R.string.matfey_str);
                break;
            case BooksId.MARK_ID:
                name = context.getResources().getString(R.string.mark_str);
                break;
            case BooksId.LUKA_ID:
                name = context.getResources().getString(R.string.luka_str);
                break;
            case BooksId.IOANNA_ID:
                name = context.getResources().getString(R.string.ioanna_str);
                break;
            case BooksId.DEYANIA_ID:
                name = context.getResources().getString(R.string.deyania_str);
                break;
            case BooksId.IAKOVA_ID:
                name = context.getResources().getString(R.string.iakova_str);
                break;
            case BooksId.ONE_PETRA_ID:
                name = context.getResources().getString(R.string.one_petra_str);
                break;
            case BooksId.TWO_PETRA_ID:
                name = context.getResources().getString(R.string.two_petra_str);
                break;
            case BooksId.ONE_IOANNA_ID:
                name = context.getResources().getString(R.string.one_ioanna_str);
                break;
            case BooksId.TWO_IOANNA_ID:
                name = context.getResources().getString(R.string.two_ioanna_str);
                break;
            case BooksId.THREE_IOANNA_ID:
                name = context.getResources().getString(R.string.three_ioanna_str);
                break;
            case BooksId.IUDY_ID:
                name = context.getResources().getString(R.string.iudy_str);
                break;
            case BooksId.RIMLAN_ID:
                name = context.getResources().getString(R.string.rimlan_str);
                break;
            case BooksId.ONE_KORINHANAM_ID:
                name = context.getResources().getString(R.string.one_korinfyanam_str);
                break;
            case BooksId.TWO_KORINHANAM_ID:
                name = context.getResources().getString(R.string.two_korinfyanam_str);
                break;
            case BooksId.GALATAM_ID:
                name = context.getResources().getString(R.string.galatam_str);
                break;
            case BooksId.EFESANAM_ID:
                name = context.getResources().getString(R.string.efesyanam_str);
                break;
            case BooksId.FILIPIYCAM_ID:
                name = context.getResources().getString(R.string.filipiycam_str);
                break;
            case BooksId.KOLOSYANAM_ID:
                name = context.getResources().getString(R.string.kolosyanam_str);
                break;
            case BooksId.ONE_FESOLONIKIYCAM_ID:
                name = context.getResources().getString(R.string.one_fessalonikiycam_str);
                break;
            case BooksId.TWO_FESOLONIKIYCAM_ID:
                name = context.getResources().getString(R.string.two_fessalonikiycam_str);
                break;
            case BooksId.ONE_TIMOFEY_ID:
                name = context.getResources().getString(R.string.one_timofeu_str);
                break;
            case BooksId.TWO_TIMOFEY_ID:
                name = context.getResources().getString(R.string.two_timofeu_str);
                break;
            case BooksId.TITU_ID:
                name = context.getResources().getString(R.string.titu_str);
                break;
            case BooksId.FILIMONU_ID:
                name = context.getResources().getString(R.string.filimonu_str);
                break;
            case BooksId.EVREYAM_ID:
                name = context.getResources().getString(R.string.evreyam_str);
                break;
            case BooksId.OTKROVENIE_ID:
                name = context.getResources().getString(R.string.otkrovenie_str);
                break;
        }

        return name;
    }

    public static int getBookIdByName(String name, Context ctx){
        if(name.equals(ctx.getString(R.string.bitie_str))){
            return BooksId.BITIE_ID;}
        if(name.equals(ctx.getString(R.string.ishod_str))){
            return BooksId.ISHOD_ID;}
        if(name.equals(ctx.getString(R.string.levit_str))){
            return BooksId.LEVIT_ID;}
        if(name.equals(ctx.getString(R.string.number_str))){
            return BooksId.NUMBER_ID;}
        if(name.equals(ctx.getString(R.string.vtorozakonie_str))){
            return BooksId.VTOROZ_ID;}
        if(name.equals(ctx.getString(R.string.iisus_navin_str))){
            return BooksId.IISUS_NAVIN_ID;}
        if(name.equals(ctx.getString(R.string.sudii_str))){
            return BooksId.SUDII_ID;}
        if(name.equals(ctx.getString(R.string.ruf_str))){
            return BooksId.RUTH_ID;}
        if(name.equals(ctx.getString(R.string.one_carstw_str))){
            return BooksId.ONE_CARSTV_ID;}
        if(name.equals(ctx.getString(R.string.two_carstw_str))){
            return BooksId.TWO_CARSTV_ID;}
        if(name.equals(ctx.getString(R.string.three_carstw_str))){
            return BooksId.THREE_CARSTV_ID;}
        if(name.equals(ctx.getString(R.string.four_carstw_str))){
            return BooksId.FOUR_CARSTV_ID;}
        if(name.equals(ctx.getString(R.string.one_paralipamenon_str))){
            return BooksId.ONE_PARALIPAM_ID;}
        if(name.equals(ctx.getString(R.string.two_paralipamenon_str))){
            return BooksId.TWO_PARALIPAM_ID;}
        if(name.equals(ctx.getString(R.string.ezdra_str))){
            return BooksId.EZDRA_ID;}
        if(name.equals(ctx.getString(R.string.neemia_str))){
            return BooksId.NEEMIA_ID;}
        if(name.equals(ctx.getString(R.string.esfir_str))){
            return BooksId.ESFIR_ID;}
        if(name.equals(ctx.getString(R.string.iov_str))){
            return BooksId.IOV_ID;}
        if(name.equals(ctx.getString(R.string.psaltir_str))){
            return BooksId.PSALTIR_ID;}
        if(name.equals(ctx.getString(R.string.pritchi_str))){
            return BooksId.PRITCHI_ID;}
        if(name.equals(ctx.getString(R.string.ecclesiast_str))){
            return BooksId.ECCL_ID;}
        if(name.equals(ctx.getString(R.string.pesni_pesney_str))){
            return BooksId.PESNI_PESNEY_ID;
        }
        if(name.equals(ctx.getString(R.string.isaia_str))){
            return BooksId.ISAIA_ID;
        }
        if(name.equals(ctx.getString(R.string.ieremia_str))){
            return BooksId.IEREMIA_ID;
        }
        if(name.equals(ctx.getString(R.string.plach_ieremii_str))){
            return BooksId.PLACH_IEREM_ID;
        }
        if(name.equals(ctx.getString(R.string.iezekiil_str))){
            return BooksId.IZEKIIL_ID;
        }
        if(name.equals(ctx.getString(R.string.daniil_str))){
            return BooksId.DANIIL_ID;
        }
        if(name.equals(ctx.getString(R.string.osia_str))){
            return BooksId.OSIA_ID;
        }
        if(name.equals(ctx.getString(R.string.ioil_str))){
            return BooksId.IOIL_ID;
        }
        if(name.equals(ctx.getString(R.string.amos_str))){
            return BooksId.AMOS_ID;
        }
        if(name.equals(ctx.getString(R.string.avdiy_str))){
            return BooksId.AVDIY_ID;
        }
        if(name.equals(ctx.getString(R.string.iona_str))){
            return BooksId.IONA_ID;
        }
        if(name.equals(ctx.getString(R.string.michey_str))){
            return BooksId.MICHEY_ID;
        }
        if(name.equals(ctx.getString(R.string.naum_str))){
            return BooksId.NAUM_ID;
        }
        if(name.equals(ctx.getString(R.string.avvakum_str))){
            return BooksId.AVVAKUM_ID;
        }
        if(name.equals(ctx.getString(R.string.sofonia_str))){
            return BooksId.SOFONIA_ID;
        }
        if(name.equals(ctx.getString(R.string.aggey_str))){
            return BooksId.AGGEY_ID;
        }
        if(name.equals(ctx.getString(R.string.zaharia_str))){
            return BooksId.ZAHARIA_ID;
        }
        if(name.equals(ctx.getString(R.string.malahia_str))){
            return BooksId.MALAKHIA_ID;
        }
        if(name.equals(ctx.getString(R.string.matfey_str))){
            return BooksId.MATFEY_ID;
        }
        if(name.equals(ctx.getString(R.string.mark_str))){
            return BooksId.MARK_ID;
        }
        if(name.equals(ctx.getString(R.string.luka_str))){
            return BooksId.LUKA_ID;
        }
        if(name.equals(ctx.getString(R.string.ioanna_str))){
            return BooksId.IOANNA_ID;
        }
        if(name.equals(ctx.getString(R.string.deyania_str))){
            return BooksId.DEYANIA_ID;
        }
        if(name.equals(ctx.getString(R.string.iakova_str))){
            return BooksId.IAKOVA_ID;
        }
        if(name.equals(ctx.getString(R.string.one_petra_str))){
            return BooksId.ONE_PETRA_ID;
        }
        if(name.equals(ctx.getString(R.string.two_petra_str))){
            return BooksId.TWO_PETRA_ID;
        }
        if(name.equals(ctx.getString(R.string.one_ioanna_str))){
            return BooksId.ONE_IOANNA_ID;
        }
        if(name.equals(ctx.getString(R.string.two_ioanna_str))){
            return BooksId.TWO_IOANNA_ID;
        }
        if(name.equals(ctx.getString(R.string.three_ioanna_str))){
            return BooksId.THREE_IOANNA_ID;
        }
        if(name.equals(ctx.getString(R.string.iudy_str))){
            return BooksId.IUDY_ID;
        }
        if(name.equals(ctx.getString(R.string.rimlan_str))){
            return BooksId.RIMLAN_ID;
        }
        if(name.equals(ctx.getString(R.string.one_korinfyanam_str))){
            return BooksId.ONE_KORINHANAM_ID;
        }
        if(name.equals(ctx.getString(R.string.two_korinfyanam_str))){
            return BooksId.TWO_KORINHANAM_ID;
        }
        if(name.equals(ctx.getString(R.string.galatam_str))){
            return BooksId.GALATAM_ID;
        }
        if(name.equals(ctx.getString(R.string.efesyanam_str))){
            return BooksId.EFESANAM_ID;
        }
        if(name.equals(ctx.getString(R.string.filipiycam_str))){
            return BooksId.FILIPIYCAM_ID;
        }
        if(name.equals(ctx.getString(R.string.kolosyanam_str))){
            return BooksId.KOLOSYANAM_ID;
        }
        if(name.equals(ctx.getString(R.string.one_fessalonikiycam_str))){
            return BooksId.ONE_FESOLONIKIYCAM_ID;
        }
        if(name.equals(ctx.getString(R.string.two_fessalonikiycam_str))){
            return BooksId.TWO_FESOLONIKIYCAM_ID;
        }
        if(name.equals(ctx.getString(R.string.one_timofeu_str))){
            return BooksId.ONE_TIMOFEY_ID;
        }
        if(name.equals(ctx.getString(R.string.two_timofeu_str))){
            return BooksId.TWO_TIMOFEY_ID;
        }
        if(name.equals(ctx.getString(R.string.titu_str))){
            return BooksId.TITU_ID;
        }
        if(name.equals(ctx.getString(R.string.filimonu_str))){
            return BooksId.FILIMONU_ID;
        }
        if(name.equals(ctx.getString(R.string.evreyam_str))){
            return BooksId.EVREYAM_ID;
        }
        if(name.equals(ctx.getString(R.string.otkrovenie_str))){
            return BooksId.OTKROVENIE_ID;
        }
        return 0;
    }

    public static String getTranslateWitchPreferences(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String translateName = BibleDB.TABLE_NAME_RST;
        if(prefs.contains(ctx.getString(R.string.pref_default_translate))){

            switch(Integer.parseInt(prefs.getString(ctx.getString(R.string.pref_default_translate), "0"))){
                case 0:
                    translateName = BibleDB.TABLE_NAME_RST;
                    break;
                case 1:
                    translateName = BibleDB.TABLE_NAME_MT;
                    break;
                case 2:
                    translateName = BibleDB.TABLE_NAME_UAT;
                    break;
                case 3:
                    translateName = BibleDB.TABLE_NAME_ENT;
                    break;
            }
        }
        return translateName;
    }

    public static String getTranslateIdWitchPreferences(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if(prefs.contains(ctx.getString(R.string.pref_default_translate))) {
            return prefs.getString(ctx.getString(R.string.pref_default_translate), "0");
        }
        return "0";
    }

    public static String getTranslateWitchPreferences(String idTranslate, Context ctx){
        String translateName = BibleDB.TABLE_NAME_RST;

        switch(Integer.parseInt(String.valueOf(idTranslate))){
            case 0:
                translateName = ctx.getString(R.string.is_rst_translate);
                break;
            case 1:
                translateName = ctx.getString(R.string.is_mt_translate);
                break;
            case 2:
                translateName = ctx.getString(R.string.is_ua_translate);
                break;
            case 3:
                translateName = ctx.getString(R.string.is_en_translate);
                break;
        }
        return translateName;
    }

    public static void showToastCenter(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
