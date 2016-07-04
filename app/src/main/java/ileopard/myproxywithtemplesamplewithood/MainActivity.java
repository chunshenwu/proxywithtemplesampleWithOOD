package ileopard.myproxywithtemplesamplewithood;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.ExpandableListViewPlayer;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.proxy.DetectItemsProxy;

public class MainActivity extends AppCompatActivity implements ExpandableListViewPlayer.IAnimatorEndCallBack {

    //View
    private RecyclerView mCardRecyclerView;
    private DetectItemsProxy mCardProxy;
    private Button mSmallButton;
    private Button mMediumButton;
    private Button mBigButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initCard();
    }

    private void initView() {
        mCardRecyclerView = (RecyclerView) findViewById(R.id.cardRecyclerView);
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initCard() {
        mCardProxy = new DetectItemsProxy(getApplicationContext(), mCardRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ExpandableListViewPlayer.IAnimatorEndCallBack cb = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCardProxy.playEachItemByAnimation(cb);
            }
        }, 1000);

    }

    @Override
    public void animationEnd() {
        this.findViewById(R.id.btnLyt).setVisibility(View.VISIBLE);
    }
}
