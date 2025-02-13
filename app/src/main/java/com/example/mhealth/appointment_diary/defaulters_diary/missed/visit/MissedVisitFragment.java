package com.example.mhealth.appointment_diary.defaulters_diary.missed.visit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mhealth.appointment_diary.R;
import com.example.mhealth.appointment_diary.models.Appointments;
import com.example.mhealth.appointment_diary.tables.Activelogin;
import com.example.mhealth.appointment_diary.tables.Ucsfadmin;
import com.example.mhealth.appointment_diary.tables.Ucsftracers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.mhealth.appointment_diary.config.Config.MISSEDVISITENDPERIOD;
import static com.example.mhealth.appointment_diary.config.Config.MISSEDVISITSTARTPERIOD;

/**
 * Created by abdullahi on 11/12/2017.
 */

public class MissedVisitFragment extends Fragment {

    long diffdate;
    private MissedVisitAdapter myadapt;
    private List<MissedVisitModel> mymesslist;

    ListView messages2;

    private static MissedVisitFragment inst;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public static MissedVisitFragment instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    public void populateListView() {


        try {


            mymesslist = new ArrayList<>();
            List<Appointments> bdy;

            String activeuname = "";
            List<Activelogin> al = Activelogin.findWithQuery(Activelogin.class, "select * from Activelogin");
            for (int a = 0; a < al.size(); a++) {
                activeuname = al.get(a).getUname();
            }
            List<Ucsftracers> ut = Ucsftracers.findWithQuery(Ucsftracers.class, "select * from Ucsftracers where uname=?", activeuname);
            List<Ucsfadmin> ua = Ucsfadmin.findWithQuery(Ucsfadmin.class, "select * from Ucsfadmin where uname=?", activeuname);

            if (ut.size() > 0) {

                bdy = Appointments.findWithQuery(Appointments.class, "Select * from Appointments where traced=? and traceruname=?", "true", activeuname);

            } else if (ua.size() > 0) {

                bdy = Appointments.findWithQuery(Appointments.class, "Select * from Appointments where traced=?", "false");

            } else {

                bdy = Appointments.findWithQuery(Appointments.class, "Select * from Appointments");

            }

//            for(int x=0;x<10;x++){
//
//                mymesslist.add(new MissedVisitModel("myccnumber"+x,"myname"+x,""+x,"myapptype"+x,"mydate"+x,"read"+x,"patientid"+x,"fileserial"+x,"informantnum"+x,"lastdatereadS"+x,"readcountS"+x));
//
//
//            }

//            mymesslist=new ArrayList<>();
//            List<Appointments> bdy = Appointments.findWithQuery(Appointments.class, "Select * from Appointments", null);


            for (int x = 0; x < bdy.size(); x++) {

                String myccnumber = bdy.get(x).getCcnumber();
                String myphone = bdy.get(x).getPhone();
                String informantS = bdy.get(x).getInformantnumber();
                String myname = bdy.get(x).getName();
                String myapptype = bdy.get(x).getAppointmenttype();
                String mydate = bdy.get(x).getDate();
                String myread = bdy.get(x).getRead();
                String patientID = bdy.get(x).getAppointmentid();
                String fileserial = bdy.get(x).getFileserial();
                String lastdatereadS = bdy.get(x).getLastdateread();
                String readcountS = bdy.get(x).getReadcount();


                SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String inputString1 = mydate;
                String inputString2 = myFormat.format(date);


                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    diffdate = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (diffdate > MISSEDVISITSTARTPERIOD && diffdate <= MISSEDVISITENDPERIOD) {
                    int readCount = Integer.parseInt(readcountS);

                    if (myread.equals("read") && readCount > 6) {

                    } else {

                        mymesslist.add(new MissedVisitModel(myccnumber, myname, myphone, myapptype, mydate, myread, patientID, fileserial, informantS, lastdatereadS, readcountS));

                    }

                }

            }

            myadapt = new MissedVisitAdapter(getContext(), mymesslist);
            messages2.setAdapter(myadapt);


        } catch (Exception e) {


        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.missedvisitfragment, container, false);

        messages2 = (ListView) view.findViewById(R.id.messages2);

        List<Appointments> myl = Appointments.findWithQuery(Appointments.class, "Select * from Appointments", null);

        if (myl.size() == 0) {


        } else {


        }


        populateListView();


        return view;
    }

    public void doSearching(CharSequence s) {
        //refreshSmsInbox();
        try {
            myadapt.getFilter().filter(s.toString());
           //Toast.makeText(getActivity(), "searching defaulter call "+s.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            Toast.makeText(getActivity(), "unable to filter: " + e, Toast.LENGTH_SHORT).show();
        }


    }


}
