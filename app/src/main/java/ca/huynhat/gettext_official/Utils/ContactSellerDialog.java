package ca.huynhat.gettext_official.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-28.
 */

public class ContactSellerDialog extends BottomSheetDialogFragment {
    private Context context;

    public ContactSellerDialog(){

    }
    //TODO: create a newInstance that accept phonenumber as a parameter



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout,container,false);

        //Take photo
        LinearLayout takePic = (LinearLayout) view.findViewById(R.id.call_seller);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                //TODO: pass in the phone number

                callIntent.setData(Uri.parse("tel:0377778888"));
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });


        //Choose photo
        LinearLayout choosePic = (LinearLayout) view.findViewById(R.id.text_message_seller);
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                //sendIntent.putExtra("sms_body", "default content");
                //sendIntent.setType("vnd.android-dir/mms-sms");
                //startActivity(sendIntent);
            }
        });

        //Scan barcode
        LinearLayout scanBarCode = (LinearLayout) view.findViewById(R.id.chat_with_seller);
        scanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return view;
    }



}
