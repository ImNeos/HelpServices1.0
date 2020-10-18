package com.isma.soli.ad.Profil;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isma.soli.ad.InternalDatabase.DBMessageSent;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Model.MessageSentClass;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageSentFragment extends Fragment {


    public MessageSentFragment() {
        // Required empty public constructor
    }
    private List<MessageSentClass> messageSentClassList = new ArrayList<>();
    RecyclerView recyclerMessage;
    Context context;
    String UserID;
    ListMyMessageSentAdapter adapter;
    private View view;
    long count=0;
    long countCheck = 0;
    private ProgressDialog loadingBar;
    HelperClass helperClass = new HelperClass();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_message_sent, container, false);
        Init();
        FillInList();
        return view;
    }

    private void Init() {
        context = getContext();


        UserID = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);
        recyclerMessage = (RecyclerView) view.findViewById(R.id.recycle_my_annonce);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerMessage.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        recyclerMessage.setLayoutManager(layoutManager);
        adapter = new ListMyMessageSentAdapter(context, messageSentClassList);
        recyclerMessage.setAdapter(adapter);
    }

    private void FillInList()
    {
        DBMessageSent.getInstance(context).fillInlist(messageSentClassList);
    }

}
class ListMyMessageSentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<MessageSentClass> messageClassList= new ArrayList<>();
    HelperClass helperClass = new HelperClass();




    public ListMyMessageSentAdapter(Context context, List<MessageSentClass> messageClassList) {
        this.context = context;
        this.messageClassList = messageClassList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_my_message, parent, false);
        return new ItemMessageSentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final String title= messageClassList.get(position).getTitle();
        final String time = messageClassList.get(position).getTime_stamp();
        if (!TextUtils.isEmpty(time))
        {
            ((ItemMessageSentHolder)holder).lbl_time.setText(helperClass.returnDataFromMillis(Long.parseLong(time)));
        }
        if (!TextUtils.isEmpty(title)) {
            ((ItemMessageSentHolder) holder).lbl_title.setText(title);
        }
        String heart ="";
        if (!TextUtils.isEmpty(messageClassList.get(position).getMessage()))
        {
            heart = "Vous : " +  messageClassList.get(position).getMessage();
        }
        else
        {
            heart = "Message vide";
        }
        ((ItemMessageSentHolder) holder).lbl_heart.setText(heart);
        ((ItemMessageSentHolder) holder).lbl_username.setText("À : " + messageClassList.get(position).getUserName());
    }



    @Override
    public int getItemCount() {
        return messageClassList.size();
    }
}
class ItemMessageSentHolder extends RecyclerView.ViewHolder {

    public TextView lbl_title, lbl_heart, lbl_time, lbl_username;


    public ItemMessageSentHolder(View itemView) {
        super(itemView);

        lbl_time = itemView.findViewById(R.id.txt_time);
        lbl_heart = itemView.findViewById(R.id.txt_heart);
        lbl_username = itemView.findViewById(R.id.txt_phonenumber);
        lbl_title = itemView.findViewById(R.id.txt_title);
    }
}
