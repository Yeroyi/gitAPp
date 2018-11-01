package com.jbufa.firefighting.Contact;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jbufa.firefighting.R;
import com.jbufa.firefighting.model.CotactPerson;
import com.jbufa.firefighting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView listV;
    private ImageButton ok_btn;
    private ImageButton back_btn;
    private ContactAdapter contactAdapter;
    private Button add_contacts;
    private EditText username;
    private EditText userPhone;
    private ArrayList<CotactPerson> cotactPeople;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ok_btn = findViewById(R.id.ok_btn);
        back_btn = findViewById(R.id.back_btn);
        listV = findViewById(R.id.act_main_list);
        add_contacts = findViewById(R.id.add_contacts);
        add_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShow();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cotactPeople.size() > 0) {
                    Toast.makeText(ContactsActivity.this, "联系人添加成功", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra("peoples", cotactPeople);
                setResult(5, intent);
                finish();
            }
        });

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(date);
        Log.e("ceshi", "时间戳：" + time);
        cotactPeople = new ArrayList<>();

        contactAdapter = new ContactAdapter(this, cotactPeople);
//        dialogShow();
        listV.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));

        listV.setAdapter(contactAdapter);
    }

    private void dialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.update_manage_dialog, null);
        ImageButton goto_cotact = v.findViewById(R.id.goto_cotact);
        goto_cotact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            }
        });
        username = v.findViewById(R.id.username);

        userPhone = v.findViewById(R.id.userPhone);


        Button btn_sure = (Button) v.findViewById(R.id.dialog_btn_sure);
        Button btn_cancel = (Button) v.findViewById(R.id.dialog_btn_cancel);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(ContactsActivity.this, "请填写姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userPhone.getText().toString().isEmpty()) {
                    Toast.makeText(ContactsActivity.this, "请填写号码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!username.getText().toString().isEmpty() && !userPhone.getText().toString().isEmpty()) {
                    if (Utils.isPhoneNumber(userPhone.getText().toString())) {
                        CotactPerson cotactPerson = new CotactPerson();
                        cotactPerson.setName(username.getText().toString());
                        cotactPerson.setPhone(userPhone.getText().toString());
                        if (cotactPeople.size() > 3) {
                            Toast.makeText(ContactsActivity.this, "最多能添加4个联系人", Toast.LENGTH_SHORT).show();
                        } else {
                            cotactPeople.add(cotactPerson);
                        }
                        contactAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ContactsActivity.this, "号码格式有误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            if (cursor.moveToPosition(0) != true) {
                return;
            }
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            if (phone != null && phone.moveToNext()) {
                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String number = usernumber.replace(" ", "");
                this.username.setText(username);
                userPhone.setText(number);
                Log.e("ceshi", "number:" + number);
                Log.e("ceshi", "username:" + username);
            }
        }
    }

}
