package com.isma.soli.ad.Profil;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.isma.soli.ad.InternalDatabase.DBMessage;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Model.MyMessageClass;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    public MessagesFragment() {
        // Required empty public constructor
    }


    private List<MyMessageClass> annonceClassList = new ArrayList<>();
    ListMyMessageAdapter adapter;
    RecyclerView recyclerMessage;
    Context context;
    String UserID;
    private View view;
    long count=0;
    long countCheck = 0;
    private ProgressDialog loadingBar;
    HelperClass helperClass = new HelperClass();

    private  boolean isShowing = true;
    private  Dialog dialog;

    ValueEventListener SyncAnnonceListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.exists()) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    RetrieveDataFromDataSnapShot(child);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {

        }
    };

    private boolean checkifDataNull(DataSnapshot child, String key)
    {
        if (child.child(key).exists())
        {
            if (!TextUtils.isEmpty(child.child(key).getValue().toString()))
            {
                Log.i("checkNull", key + "Ok");
                return true;


            }
            else
            {
                Log.i("checkNull", key + "null");
           //     IncremeentCount();
                return false;
            }
        }
        else
        {
            Log.i("checkNull", key + "null");
         //   IncremeentCount();
            return false;
        }
    }
    public void setLoadingBar(String title, String message)
    {
        if (!loadingBar.isShowing())
        {
            loadingBar = new ProgressDialog(context);
            loadingBar.setCancelable(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
    }
   /* private void IncremeentCount()
    {
        countCheck++;
        if (countCheck == count)
        {
            if (loadingBar.isShowing()) {
                loadingBar.dismiss();
            }
            helperClass.simpleDialog("Annonce", "Aucune annonce n'a été trouvée à proximité de chez vous.", context);
        }
    }*/

    private void RetrieveDataFromDataSnapShot(DataSnapshot child)
    {
        if (!DBMessage.getInstance(context).checkAlreadyExist(child.getKey())) {
            if (!checkifDataNull(child, StaticValues.UserName)) {
                return;
            }
            if (!checkifDataNull(child, StaticValues.Time)) {
                return;
            }
            if (!checkifDataNull(child, StaticValues.PhoneNumber)) {
                return;
            }
            if (!checkifDataNull(child, StaticValues.annonce_title)) {
                return;
            }

            Log.i("CheckAnnonce", "In");
            MyMessageClass messageClass = new MyMessageClass();
            if (child.child(StaticValues.UserName).exists()) {
                messageClass.setName(child.child(StaticValues.UserName).getValue().toString());
            }
            if (child.child(StaticValues.Time).exists()) {
                messageClass.setTime(child.child(StaticValues.Time).getValue().toString());

            } else {
                messageClass.setTime("");
            }
            if (child.child(StaticValues.PhoneNumber).exists()) {
                messageClass.setPhonenumber(child.child(StaticValues.PhoneNumber).getValue().toString());

            }
            if (child.child(StaticValues.annonce_title).exists()) {
                messageClass.setMessage(child.child(StaticValues.annonce_title).getValue().toString());

            }
            if (child.child(StaticValues.TextAnnonce).exists()) {
                messageClass.setText(child.child(StaticValues.TextAnnonce).getValue().toString());

            }
            messageClass.setKey(child.getKey());
            DBMessage.getInstance(context).addElementTodB(messageClass);
            //TODO
            DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.SyncMessage, child.child(StaticValues.Time).getValue().toString());
            annonceClassList.add(0, messageClass);
            if (dialog != null)
            {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
            adapter.notifyDataSetChanged();

            //Add to list + to DB if user memory is down
        }
        else
        {
            Log.i("CheckAnnonce", "Out");

        }
    }
    private void FillInList()
    {
        DBMessage.getInstance(context).fillInlist(annonceClassList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        view= inflater.inflate(R.layout.fragment_my_messages, container, false);
        Init();
        FillInList();
        SyncMessage();

        return  view;
    }
    private void SyncMessage()
    {

        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(StaticValues.SyncMessage))
        {
            String Count = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncMessage);

            Log.i("CheckAnnonce", "Count exist" + Count);

            long time = Long.parseLong(Count);
            FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child(UserID).
                    orderByChild(StaticValues.Time).startAt(time+1)
        .addListenerForSingleValueEvent(SyncAnnonceListener);

        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child(UserID).addListenerForSingleValueEvent(SyncAnnonceListener);
            Log.i("CheckAnnonce", "Count does not exist");
        }
    }
    private void Init() {

        context = getContext();


        UserID = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);
        recyclerMessage = (RecyclerView) view.findViewById(R.id.recycle_my_annonce);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerMessage.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        recyclerMessage.setLayoutManager(layoutManager);
        adapter = new ListMyMessageAdapter(context, annonceClassList);
        recyclerMessage.setAdapter(adapter);
    }



    @Override
    public void setMenuVisibility(final boolean visible) {

        if (visible)
        {
           /* if (annonceClassList.size() == 0)
            {
               try {

                    if (isShowing)
                    {
                        dialog = new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setTitle("Aucun message")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                        isShowing = false;
                    }
                }catch (Exception e){}
            }*/
        }

        super.setMenuVisibility(visible);
    }

}
class ListMyMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<MyMessageClass> messageClassList= new ArrayList<>();
    HelperClass helperClass = new HelperClass();


    String Items []= {"Contacter la personne", "Supprimer le message"};


    public ListMyMessageAdapter(Context context, List<MyMessageClass> messageClassList) {
        this.context = context;
        this.messageClassList = messageClassList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_my_message, parent, false);
        return new ItemMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final String title= messageClassList.get(position).getMessage();
        String phonenumber = "Numéro de téléphone : " + messageClassList.get(position).getPhonenumber();
        final String time = messageClassList.get(position).getTime();
        if (!TextUtils.isEmpty(time))
        {

            ((ItemMessageHolder)holder).lbl_time.setText(helperClass.returnDataFromMillis(Long.parseLong(time)));
        }

        ((ItemMessageHolder)holder).lbl_title.setText(title);

        ((ItemMessageHolder) holder).lbl_phonenumber.setText(phonenumber);

        String heart ="";
        if (!TextUtils.isEmpty(messageClassList.get(position).getText()))
        {
             heart = messageClassList.get(position).getName() + " : "+ messageClassList.get(position).getText();

        }
        else
        {
             heart = messageClassList.get(position).getName() + " a répondu à votre annonce !";
        }
        ((ItemMessageHolder) holder).lbl_heart.setText(heart);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new LovelyChoiceDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Options")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(Items, new LovelyChoiceDialog.OnItemSelectedListener<String>()
                        {
                            @Override
                            public void onItemSelected(int pos, String item) {
                                switch (pos)
                                {
                                    case 0:

                                        helperClass.ChooseBetweenCallORSendSms(messageClassList.get(position).getPhonenumber(), messageClassList.get(position).getName(), context,
                                                "Bonjour, j'ai reçu votre message sur Help\u0026Services concernant l'annonce \"" + title+ "\".\n");
                                        break;

                                    case 1:

                                        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                                .setTopColorRes(R.color.colorPrimary)
                                                .setTitle("Voulez-vous vraiment supprimer le message?")
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .setPositiveButton("Oui", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {

                                                            DBMessage.getInstance(context).Deletecom(messageClassList.get(position).getKey());
                                                            FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID))
                                                                    .child(messageClassList.get(position).getKey()).removeValue();
                                                            messageClassList.remove(position);
                                                            notifyDataSetChanged();
                                                        }catch (Exception e){}
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null)
                                                .show();
                                        break;

                                }
                            }
                        })
                        .show();



            }
        });
    }

    @Override
    public int getItemCount() {
        return messageClassList.size();
    }
}
class ItemMessageHolder extends RecyclerView.ViewHolder
{

    public TextView lbl_title, lbl_heart, lbl_time, lbl_phonenumber;


    public ItemMessageHolder(View itemView) {
        super(itemView);

        lbl_time = itemView.findViewById(R.id.txt_time);
        lbl_heart = itemView.findViewById(R.id.txt_heart);
        lbl_phonenumber = itemView.findViewById(R.id.txt_phonenumber);
        lbl_title = itemView.findViewById(R.id.txt_title);
    }
}