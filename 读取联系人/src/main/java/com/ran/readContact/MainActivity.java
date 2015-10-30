package com.ran.readContact;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv_list);

        List<Map<String, String>> datas = readContact();

        lv.setAdapter(new SimpleAdapter(this, datas, R.layout.contact_list_item, new String[]{"name", "phone"},
                new int[]{R.id.tv_name, R.id.tv_phone}));
    }

    /**
     * 读取联系人
     *
     * @return
     */
    private List<Map<String, String>> readContact() {
        // 首先,从raw_contacts中读取联系人的id("contact_id")
        // 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
        // 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        // 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawCursor = getContentResolver().query(rawContactsUri, new String[]{"contact_id"}, null, null, null);

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (null != rawCursor) {
            while (rawCursor.moveToNext()) {
                String contactId = rawCursor.getString(0);
                if (TextUtils.isEmpty(contactId)){
                    continue;
                }
                Map<String, String> map = new HashMap<String, String>(2);
                // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                Cursor dataCursor = getContentResolver().query(dataUri, new String[]{"data1", "mimetype"}, "contact_id = ?", new String[]{contactId}, null);
                if (null != dataCursor) {
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name"
                                .equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawCursor.close();
        }
        return list;
    }
}