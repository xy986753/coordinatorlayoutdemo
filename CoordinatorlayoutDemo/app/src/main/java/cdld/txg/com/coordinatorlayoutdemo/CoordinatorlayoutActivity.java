package cdld.txg.com.coordinatorlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorlayoutActivity extends AppCompatActivity {
    private RecyclerView lv_allcard;
    private List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        lv_allcard = (RecyclerView)findViewById(R.id.rv);
        for (int i = 0; i < 33; i++) {
            list.add("三个和尚都搞活动覆盖偶尔会给哦人格和个人合格" + i);
        }
        StringrvAdapter stringListAdapter = new StringrvAdapter(list,this);
        lv_allcard.setAdapter(stringListAdapter);
    }
}
