package com.isma.soli.ad;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.LoginScreen.LoginActivity;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpliFragment extends Fragment {


    public ExpliFragment() {
        // Required empty public constructor
    }

    int myInt;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_expli, container, false);

        myInt = getArguments().getInt("myInt");


        InitFields();
        return  view;
    }

    private void InitFields()
    {
        final TextView title = (TextView) view.findViewById(R.id.title);
        final TextView message = (TextView) view.findViewById(R.id.message);
        final Button btn_next = (Button) view.findViewById(R.id.btn_go_next);
        final Button btn_prec = (Button) view.findViewById(R.id.btn_precedent);

        final ImageView imageView = (ImageView) view.findViewById(R.id.im_icon);

        final CircleImageView circle_left = (CircleImageView) view.findViewById(R.id.circle_left);
        final CircleImageView circle_center = (CircleImageView) view.findViewById(R.id.circle_center);
        final CircleImageView circle_right = (CircleImageView) view.findViewById(R.id.circle_right);

        switch (myInt)
        {
            case 0 :
            {
                btn_prec.setText(R.string.annuler);
                btn_next.setText(R.string.jaicompris);

                imageView.setImageResource(R.drawable.ic_contact_phone_black_24dp);

                circle_left.setImageResource(R.drawable.icons8_circlefull);
                circle_center.setImageResource(R.drawable.icons8_circle);
                circle_right.setImageResource(R.drawable.icons8_circle);
                title.setText("Introduction");
                message.setText("Bonjour et bienvenue sur Help&Services. Nous vous souhaitons une agréable expérience.");
                btn_next.setText(R.string.expli_go_next);
                //
                break;
            }
            case 1 :
            {
                circle_left.setImageResource(R.drawable.icons8_circle);
                circle_center.setImageResource(R.drawable.icons8_circlefull);

                imageView.setImageResource(R.drawable.ic_forum_black_24dp);

                circle_right.setImageResource(R.drawable.icons8_circle);
                title.setText("Principe de l'application");
                message.setText("Le principe est très simple. Il est possible de poster une annonce afin de\n-Demander un service\n-Proposer un service\n\nIl est également possible de consulter l'ensemble des annonces. Deux personnes rentrent en contact directement via l'application ou via un numéro de téléphone si cela est autorisé par l'utilisateur.");
                btn_next.setText(R.string.expli_go_next);
                btn_prec.setText(R.string.precedent);
                break;
            }
            case 2 :
            {
                circle_left.setImageResource(R.drawable.icons8_circle);
                circle_center.setImageResource(R.drawable.icons8_circle);

                imageView.setImageResource(R.drawable.icons8_sign);
                btn_prec.setText(R.string.precedent);
                circle_right.setImageResource(R.drawable.icons8_circlefull);
                title.setText("Charte de confidentialité");

                String text = "Dans cet application plusieurs de vos données seront utilisées afin d'améliorer votre expérience.\nElles ne seront en aucun cas utilisées sans votre permission et votre connaissance.\nPour plus d'informations, cliquez ici pour accéder à notre page Facebook.";
                SpannableString ss = new SpannableString(text);
                ForegroundColorSpan fscblue = new ForegroundColorSpan(Color.BLUE);
                String tab[] = text.split("cliquez");
                ss.setSpan(fscblue, tab[0].length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setText(ss);
                btn_next.setText("Accepter");
                break;
            }
        }
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  String FACEBOOK_URL = "https://www.facebook.com/HelpServices-111304393854486";
                  String FACEBOOK_PAGE_ID = "Help&Services";



                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getContext(), FACEBOOK_URL, FACEBOOK_PAGE_ID);
                facebookIntent.setData(Uri.parse(facebookUrl));
                try {
                    getContext().startActivity(facebookIntent);
                }catch (Exception e){}

               /* String url = "https://www.facebook.com/HelpServices-111304393854486";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                try {
                    getContext().startActivity(i);
                }catch (Exception e){}*/
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (myInt !=2) {
                    ((MainActivity) getActivity()).ChangeViewFromFragment(true, 750);
                   // ((MainActivity) getActivity()).SwitchViewPageronClick(myInt + 1);
                }
                else{
                    HelperClass helperClass = new HelperClass();
                    Toast.makeText(getContext(), "Accepté !", Toast.LENGTH_SHORT).show();
                    DBSimpleIntel.getInstance(getContext()).addElementTodB(StaticValues.Confidentialite, "Ok");
                    helperClass.SendUserToOtherActivityFragmentAndfinish(getContext(), MapsActivity.class);
                }

            }
        });
        btn_prec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myInt != 0)
                {
                    ((MainActivity) getActivity()).ChangeViewFromFragment(false,750);
                }
            }
        });
    }

    public String getFacebookPageURL(Context context, String FACEBOOK_URL, String FACEBOOK_PAGE_ID) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void onResume() {

        ((MainActivity) getActivity()).IncCount();
        Log.i("OnResumeFrag", ""+myInt);
        super.onResume();
    }
}
