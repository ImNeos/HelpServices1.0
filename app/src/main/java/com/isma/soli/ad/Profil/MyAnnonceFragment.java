
package com.isma.soli.ad.Profil;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.isma.soli.ad.InternalDatabase.DBPostedAnnonce;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Model.AnnonceClass;
import com.isma.soli.ad.Model.MyAnnonceClass;
import com.isma.soli.ad.PostAnnonce.CreateAnnonceActivity;
import com.isma.soli.ad.R;

import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAnnonceFragment extends Fragment {


    public MyAnnonceFragment()
    {
        // Required empty public constructor
    }

    private ProgressDialog loadingBar;
    private List<MyAnnonceClass> annonceClassList = new ArrayList<>();
    ListMyAnnonceAdapter adapter;
    RecyclerView recyclerHelp;
    Context context;
    String UserID;
    private View view;
    ContextWrapper cw ;
    File directory ;

    private boolean isShowing = false;


    ValueEventListener SyncAnnonceListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            RetrieveDataFromDataSnapShot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {

        }
    };

    private void RetrieveDataFromDataSnapShot()
    {
        //Add to list + to DB if user memory is down
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my_annonce, container, false);
        Init();
        FillInList();

        //SyncAnnonce();

        return view;
    }

    private void SyncAnnonce()
    {
        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(StaticValues.SyncAnnonceCount))
        {
            String Count = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncAnnonceCount);

            Log.i("CheckAnnonce", "Count exist" + Count);
            FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(UserID).child(StaticValues.annonce).orderByValue().startAt(Count+1)
                    .addListenerForSingleValueEvent(SyncAnnonceListener);
        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(UserID).child(StaticValues.annonce).addListenerForSingleValueEvent(SyncAnnonceListener);
            Log.i("CheckAnnonce", "Count does not exist");
        }
    }

    private void FillInList()
    {
        DBPostedAnnonce.getInstance(context).fillInlist(annonceClassList);
        if (annonceClassList.size() == 0 && isShowing)
        {
            try {

                new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Annonce")
                        .setMessage("Aucune annonce publiée.")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Créer une annonce", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HelperClass helperClass = new HelperClass();
                                helperClass.SendUserToOtherActivityFragment(context, CreateAnnonceActivity.class);
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }catch (Exception e){}
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        context = getContext();
        if (visible) {
            isShowing = true;
        }
        else
        {
            isShowing = false;
        }

        super.setMenuVisibility(visible);
    }

    private void Init() {

        context = getContext();

        cw = new ContextWrapper(context);
        directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);

        UserID = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);
        recyclerHelp = (RecyclerView) view.findViewById(R.id.recycle_my_annonce);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerHelp.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        recyclerHelp.setLayoutManager(layoutManager);
        adapter = new ListMyAnnonceAdapter(context, annonceClassList);
        recyclerHelp.setAdapter(adapter);
    }

}
class ListMyAnnonceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<MyAnnonceClass> annonceClassList = new ArrayList<>();
    HelperClass helperClass = new HelperClass();

    String [] items = {"Modifier", "Supprimer", "Se mettre en sourdine"};

    String [] itemsOutOfDate = {"Modifier", "Supprimer", "Relancer l'annonce"};

    String [] itemsSourdine = {"Modifier", "Supprimer", "Relancer l'annonce"};
    int pos = 0;
    ContextWrapper cw ;
    File directory ;

    public ListMyAnnonceAdapter(Context context, List<MyAnnonceClass> annonceClassList) {
        this.context = context;
        this.annonceClassList = annonceClassList;
        cw = new ContextWrapper(context);
        directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_my_annonce, parent, false);
        return new ItemMyAnnonceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {




        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(annonceClassList.get(position).getKey()+ StaticValues.CreateLoca))
        {
            ((ItemMyAnnonceHolder) holder).lbl_euros.setText("€ " + "(Livraison possible)");

        }
        else
        {
            ((ItemMyAnnonceHolder) holder).lbl_euros.setText("€");

        }


        String time = annonceClassList.get(position).getTime();



        ((ItemMyAnnonceHolder) holder).lbl_heart.setText(annonceClassList.get(position).getHeart());
        String price = annonceClassList.get(position).getPrix();
        if (TextUtils.isEmpty(price))
        {
            ((ItemMyAnnonceHolder) holder).lbl_price.setText("0");
        }
        else {
            if (price.equals(StaticValues.EmptyTag)) {

            } else {
                ((ItemMyAnnonceHolder) holder).lbl_price.setText(annonceClassList.get(position).getPrix());

            }
        }
        ((ItemMyAnnonceHolder) holder).lbl_title.setText(annonceClassList.get(position).getTitle());


        if (time.equals(StaticValues.EmptyTag))
        {
            ((ItemMyAnnonceHolder) holder).lbl_time.setText("Annonce en sourdine !");

        }
        else {

            Long timeConversion = Long.parseLong(time);
            Long currenttime = Long.parseLong(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));
            ((ItemMyAnnonceHolder) holder).lbl_time.setText(helperClass.returnDataFromMillis(Long.parseLong(time)));
        }


            //   if (currenttime - timeConversion < StaticValues.TimeOut )
     //   {

            if (annonceClassList.get(position).getType().equals("0"))
            {
                //demandeur
              //  ((ItemMyAnnonceHolder)holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                ((ItemMyAnnonceHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

            }
            else
            {
            //    ((ItemMyAnnonceHolder)holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                ((ItemMyAnnonceHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

                //proposeur

            }


        File myImageFile = new File(directory, annonceClassList.get(position).getKey()+".jpg");

        if(myImageFile.exists())
        {


            ((ItemMyAnnonceHolder) holder).imageView.setVisibility(View.VISIBLE);
            ((ItemMyAnnonceHolder) holder).frameLayout.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.fromFile(myImageFile)) // Uri of the picture
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(((ItemMyAnnonceHolder)holder).imageView);
        }
        else
        {
            ((ItemMyAnnonceHolder) holder).imageView.setVisibility(View.GONE);
            ((ItemMyAnnonceHolder) holder).frameLayout.setVisibility(View.GONE);

        }

       // }
       // else
       // {
       //     ((ItemMyAnnonceHolder)holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));

        /*    ((ItemMyAnnonceHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            ((ItemMyAnnonceHolder) holder).lbl_time.setText("L'annonce est périmée");
            //set backround in red

            //delete annonce
            //TODO Retravailler la suprresion
            String key = annonceClassList.get(position).getKey();
            String userID = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);

            String query;
            if (annonceClassList.get(position).getType().equals("0"))
            {
                query = StaticValues.Demandeur;
            }
            else
            {
                query= StaticValues.Offreur;
            }
            helperClass.DeleteAnnonce(query, key, context, annonceClassList.get(position).getPostcode(), userID);

            if (!DBSimpleIntel.getInstance(context).checkAlreadyExist(key)) {
                DBSimpleIntel.getInstance(context).addElementTodB(key, StaticValues.OutOfDate);
            }*/
      //  }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String []ite;
                final String key = annonceClassList.get(position).getKey();
                if (annonceClassList.get(position).getTime().equals(StaticValues.EmptyTag))
                {
                    ite = itemsSourdine;
                }
                else
                {
                    ite = items;
                }

                new LovelyChoiceDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Options")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(ite, new LovelyChoiceDialog.OnItemSelectedListener<String>()
                        {
                            @Override
                            public void onItemSelected(int pos, String item) {
                                switch (pos)
                                {
                                    case 0:
                                        Intent intent = new Intent(context, CreateAnnonceActivity.class);
                                        intent.putExtra(StaticValues.IntentModifAnnonce, "True");
                                        intent.putExtra(StaticValues.IntentModifHeart, annonceClassList.get(position).getHeart());
                                        //  intent.putExtra(StaticValues.IntentModifPostCode, annonceClassList.get(position).getPostcode());
                                        intent.putExtra(StaticValues.IntentModifPrix, annonceClassList.get(position).getPrix());
                                        intent.putExtra(StaticValues.IntentModifTitle, annonceClassList.get(position).getTitle());
                                        intent.putExtra(StaticValues.IntentModifKey, annonceClassList.get(position).getKey());
                                        intent.putExtra(StaticValues.IntentModifType, annonceClassList.get(position).getType());
                                        intent.putExtra(StaticValues.IntentModifTime, annonceClassList.get(position).getTime());
                                        File myImageFile = new File(directory, annonceClassList.get(position).getKey()+".jpg");

                                        if (myImageFile.exists())
                                        {
                                            try
                                            {
                                                Log.i("CheckURI", Uri.fromFile(myImageFile).toString());
                                                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastImageUri, Uri.fromFile(myImageFile).toString());
                                            }
                                            catch (Exception e)
                                            {

                                            }
                                        }
                                        context.startActivity(intent);

                                        break;

                                    case 1:
                                        if (helperClass.isNetworkAvailableDialog(context))
                                        {
                                            //TODO Retravailler
                                            new AlertDialog.Builder(context)
                                                    .setTitle(annonceClassList.get(position).getTitle())
                                                    .setMessage("Êtes vous sûr de vouloir supprimer l'annonce?")

                                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {
                                                            String key = annonceClassList.get(position).getKey();
                                                            String userID = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);

                                                            String query;
                                                            if (annonceClassList.get(position).getType().equals("0"))
                                                            {
                                                                query = StaticValues.Demandeur;
                                                            }
                                                            else
                                                            {
                                                                query= StaticValues.Offreur;
                                                            }
                                                            helperClass.DeleteAnnonce(query, key, context, annonceClassList.get(position).getPostcode(), userID);
                                                            DBPostedAnnonce.getInstance(context).delete(key);
                                                            annonceClassList.remove(position);
                                                            notifyDataSetChanged();
                                                            Toast.makeText(context, "Annonce supprimée ! ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i)
                                                        {

                                                        }
                                                    })


                                                    .setIcon(android.R.drawable.ic_dialog_info)
                                                    .show();
                                        }
                                        break;
                                    case 2 :
                                    {

                                        if (helperClass.isNetworkAvailableDialog(context))
                                        {

                                            if (annonceClassList.get(position).getTime().equals(StaticValues.EmptyTag))
                                            {
                                                //RELANCER
                                                try {
                                                    String type = annonceClassList.get(position).getType();
                                                    if (type.equals("0")) {
                                                        type = StaticValues.Demandeur;
                                                    } else {
                                                        type = StaticValues.Offreur;
                                                    }
                                                    DatabaseReference DistRef = FirebaseDatabase.getInstance().getReference().child(StaticValues.Distance).child(StaticValues.Interval).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country));
                                                    String center = annonceClassList.get(position).getPostcode();
                                                    DistRef = DistRef.child(type).child(center);
                                                    final HashMap<String, Object> addIDAnn = new HashMap<>();

                                                    float La = Float.parseFloat(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLatitude));
                                                    float Lo = Float.parseFloat(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLongitude));
                                                    addIDAnn.put(StaticValues.LastLatitude, (float) La);
                                                    addIDAnn.put(StaticValues.LastLongitude, (float) Lo);

                                                    DistRef.child(annonceClassList.get(position).getKey()).setValue(addIDAnn);
                                                    //LOCA AUSSI !!!

                                                    FirebaseDatabase.getInstance().getReference().child(StaticValues.LocaPath).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country))
                                                            .child(type + "l").child(key).setValue(addIDAnn);


                                                    AnnonceClass annonceClass = new AnnonceClass();
                                                    annonceClass.setType(annonceClassList.get(position).getType());
                                                    annonceClass.setPrix(annonceClassList.get(position).getPrix());
                                                    annonceClass.setTitle(annonceClassList.get(position).getTitle());
                                                    annonceClass.setHeart(annonceClassList.get(position).getHeart());
                                                    annonceClass.setTime(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));
                                                    annonceClass.setKey(annonceClassList.get(position).getKey());

                                                    annonceClassList.get(position).setTime(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));
                                                    DBPostedAnnonce.getInstance(context).addElementTodB(annonceClass, annonceClassList.get(position).getKey(), center);


                                                    Toast.makeText(context, "Annonce relancée ! ", Toast.LENGTH_LONG).show();
                                                    notifyDataSetChanged();
                                                }catch (Exception e){}
                                            }
                                            else
                                            {
                                                //Se mettre en sourdine !!
                                                try {
                                                    String type = annonceClassList.get(position).getType();
                                                    if (type.equals("0")) {
                                                        type = StaticValues.Demandeur;
                                                    } else {
                                                        type = StaticValues.Offreur;
                                                    }
                                                    final String finalType = type;
                                                    new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                                            .setTopColorRes(R.color.colorPrimary)
                                                            .setIcon(android.R.drawable.ic_dialog_info)
                                                            .setTitle("Mise en sourdine")
                                                            .setMessage("Etes-vous sûr de vous mettre en sourdine?")
                                                            .setPositiveButton("Oui", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String center = annonceClassList.get(position).getPostcode();
                                                                    if (!TextUtils.isEmpty(center)) {

                                                                        //TODO AUSSI LA LIVRAISON !!!!


                                                                        FirebaseDatabase.getInstance().getReference().child(StaticValues.Distance).child(StaticValues.Interval).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country))
                                                                                .child(finalType).child(center).child(key).removeValue();

                                                                        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(key + StaticValues.CreateLoca)) {
                                                                            FirebaseDatabase.getInstance().getReference().child(StaticValues.LocaPath).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country))
                                                                                    .child(finalType + "l").child(key).removeValue();
                                                                        }

                                                                        Toast.makeText(context, "Annonce mise en sourdine ! ", Toast.LENGTH_LONG).show();
                                                                        AnnonceClass annonceClass = new AnnonceClass();
                                                                        annonceClass.setType(annonceClassList.get(position).getType());
                                                                        annonceClass.setPrix(annonceClassList.get(position).getPrix());
                                                                        annonceClass.setTitle(annonceClassList.get(position).getTitle());
                                                                        annonceClass.setHeart(annonceClassList.get(position).getHeart());
                                                                        annonceClass.setTime(StaticValues.EmptyTag);

                                                                        annonceClass.setKey(annonceClassList.get(position).getKey());

                                                                        annonceClassList.get(position).setTime(StaticValues.EmptyTag);

                                                                        DBPostedAnnonce.getInstance(context).addElementTodB(annonceClass, annonceClassList.get(position).getKey(), center);
                                                                        notifyDataSetChanged();
                                                                    } else {
                                                                        Toast.makeText(context, "Erreur lors de la mise en sourdine...", Toast.LENGTH_LONG).show();

                                                                    }
                                                                }
                                                            })
                                                            .setNegativeButton("Non", null)
                                                            .show();
                                                }catch (Exception e){}

                                            }
                                            /*//TODO retravailler
                                            long time = Long.parseLong(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));


                                            final HashMap<String, Object> addAnnonce = new HashMap<>();

                                            addAnnonce.put(StaticValues.annonce, annonceClassList.get(position).getHeart());
                                            addAnnonce.put(StaticValues.annonce_title, annonceClassList.get(position).getTitle());
                                            // addAnnonce.put(StaticValues.postalCode, Integer.parseInt(annonceClassList.get(position).getPostcode()));
                                            // addAnnonce.put(StaticValues.LastLongitude, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLongitude));
                                            // addAnnonce.put(StaticValues.LastLatitude, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLatitude));
                                            addAnnonce.put(StaticValues.UserID, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID));
                                            addAnnonce.put(StaticValues.Time, time);
                                            addAnnonce.put(StaticValues.prix, Float.parseFloat(annonceClassList.get(position).getPrix()));


                                            FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID))
                                                    .child(StaticValues.annonce).child(key).setValue(Integer.parseInt(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncAnnonceCount)));

                                            FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(key).setValue(addAnnonce);


                                            annonceClassList.get(position).setTime(Long.toString(time));
                                            DBPostedAnnonce.getInstance(context).updatetime(key, Long.toString(time));
                                            DBSimpleIntel.getInstance(context).Deletecom(key);
                                            notifyDataSetChanged();*/
                                        }
                                        break;
                                    }

                                }
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return annonceClassList.size();
    }
}
class ItemMyAnnonceHolder extends RecyclerView.ViewHolder {

    public TextView lbl_title, lbl_heart, lbl_price, lbl_time, lbl_coup_de_pouce, lbl_euros;

    public CardView cardView;

    FrameLayout frameLayout;
    ImageView imageView, im_back;


    public ItemMyAnnonceHolder(View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.carview);

        lbl_time = itemView.findViewById(R.id.txt_time);
        lbl_heart = itemView.findViewById(R.id.txt_heart);
        lbl_price = itemView.findViewById(R.id.txt_prix);
        lbl_title = itemView.findViewById(R.id.txt_title);

        lbl_coup_de_pouce = itemView.findViewById(R.id.LayPouce);

        frameLayout = itemView.findViewById(R.id.frame_back);

        imageView = itemView.findViewById(R.id.imageview);
        lbl_euros = itemView.findViewById(R.id.lbl_euros);






    }
}


