package com.example.cache;

import android.print.PrinterId;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    List<String> urlList = new ArrayList<String>();
    private int prefetchNum = 2;
    private int start_index;
    private int end_index;
    private int mFirstVisibleItem = 0;
    private int mVisibleItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        initData();

        //使用框架
//        Picasso.with(MainActivity.this).load(imgURl).into(mIv);
//        UseTimeTool.getInstance().start();


        //自己做的: 上下文->路径->控件
        final ImageAdapter adapter = new ImageAdapter(this,0, urlList);//新建并配置ArrayAapeter
        mListView.setAdapter(adapter);


        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    for(int i = start_index; i < mFirstVisibleItem; i++) {
                        ImageUtil.with(MainActivity.this).load(urlList.get(i)).prefetch();
                    }
                    for(int i = mFirstVisibleItem + mVisibleItemCount; i < end_index; i++) {
                        ImageUtil.with(MainActivity.this).load(urlList.get(i)).prefetch();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                start_index = Math.max(0, firstVisibleItem - prefetchNum);
                end_index = Math.min(firstVisibleItem + visibleItemCount + prefetchNum, urlList.size() - 1);
                mFirstVisibleItem = firstVisibleItem;
                mVisibleItemCount = visibleItemCount;
            }
        });
    }

    private List<String> initData() {
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4228542970,1725444700&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1072325142,3221163279&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1146748633,629239788&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836422534&di=a4bdb33ec929f99be6e6f3e6c21edc26&imgtype=0&src=http%3A%2F%2Fimg3.redocn.com%2Ftupian%2F20150606%2Fhengxiangmubantietu_3761982.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2328722018,1154947414&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3726386602,3899424628&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552717858&di=534d9ebaa716b4cd151a2c2d95f05205&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F13%2F99%2F73%2F04c58PICiBC_1024.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552724688&di=10dd45c895ba9a1f9856f691ff7be67a&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F11%2F39%2F47%2F09B58PICPVq.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2218526674,3033754369&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836990267&di=63e8b0140d35d2e9f29008f42f8314bb&imgtype=0&src=http%3A%2F%2Fimages.china.cn%2Fattachement%2Fjpg%2Fsite1000%2F20170924%2F6c0b840a25301b31b4b51a.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4266762370,3244059322&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3750355673,536097549&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838606438&di=dfbf8d322db2e41fdbc879a5101f726b&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F1c92af15e7e23955b70cd353524222b79704eb7c.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836938494&di=fc454c0d01a28f0fedd3a266ea3e22ee&imgtype=0&src=http%3A%2F%2Fimg009.hc360.cn%2Fm2%2FM01%2FE5%2FF2%2FwKhQcVRBn_mEWXTKAAAAAKh0wO4790.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2108637241,1993954056&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836422534&di=a4bdb33ec929f99be6e6f3e6c21edc26&imgtype=0&src=http%3A%2F%2Fimg3.redocn.com%2Ftupian%2F20150606%2Fhengxiangmubantietu_3761982.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1938756251,2755438251&fm=11&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3835009885,1226818710&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431945&di=42b88ab656ef19ce3a8a2627c35376eb&imgtype=jpg&er=1&src=http%3A%2F%2Fimage5.huangye88.com%2F2014%2F11%2F01%2F675f3ec50625b79b.JPG");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838192674&di=acf3f54188165758b1286bd34a51f766&imgtype=0&src=http%3A%2F%2Fm.360buyimg.com%2Fn12%2Fg10%2FM00%2F1E%2F1E%2FrBEQWVNt8ZkIAAAAAAEqtQDCwL4AAGEMgAZFzIAASrN125.jpg%2521q70.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2218526674,3033754369&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2496089338,1929248438&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554433004&di=4430b8447f423cc70925e6f533994629&imgtype=jpg&er=1&src=http%3A%2F%2Fimage.tupian114.com%2F20130110%2F11471928.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836938494&di=fc454c0d01a28f0fedd3a266ea3e22ee&imgtype=0&src=http%3A%2F%2Fimg009.hc360.cn%2Fm2%2FM01%2FE5%2FF2%2FwKhQcVRBn_mEWXTKAAAAAKh0wO4790.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2187275096,1491836145&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2496089338,1929248438&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552720723&di=5af66bfb92d7c10afc1c299fc660156d&imgtype=jpg&er=1&src=http%3A%2F%2Fdown1.sucaitianxia.net%2Feps%2F55%2Feps16077.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552724688&di=10dd45c895ba9a1f9856f691ff7be67a&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F11%2F39%2F47%2F09B58PICPVq.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552717858&di=534d9ebaa716b4cd151a2c2d95f05205&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F13%2F99%2F73%2F04c58PICiBC_1024.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3750355673,536097549&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2108637241,1993954056&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431329&di=cac4c50962f2faf92ce4ff1a29c73583&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.downyi.com%2Fupload%2Fruanjian%2Fhengxiangtupianlunbo-jquery.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1938756251,2755438251&fm=11&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3835009885,1226818710&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838192674&di=acf3f54188165758b1286bd34a51f766&imgtype=0&src=http%3A%2F%2Fm.360buyimg.com%2Fn12%2Fg10%2FM00%2F1E%2F1E%2FrBEQWVNt8ZkIAAAAAAEqtQDCwL4AAGEMgAZFzIAASrN125.jpg%2521q70.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2187275096,1491836145&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2496089338,1929248438&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431945&di=42b88ab656ef19ce3a8a2627c35376eb&imgtype=jpg&er=1&src=http%3A%2F%2Fimage5.huangye88.com%2F2014%2F11%2F01%2F675f3ec50625b79b.JPG");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836009438&di=a27732f8ad261034fd8b12f442a82be0&imgtype=0&src=http%3A%2F%2Fmeiti.fabumao.cn%2F1135652%2F20190202155802951.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4228542970,1725444700&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1072325142,3221163279&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552717858&di=534d9ebaa716b4cd151a2c2d95f05205&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F13%2F99%2F73%2F04c58PICiBC_1024.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1228574751,2869847369&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836009438&di=a27732f8ad261034fd8b12f442a82be0&imgtype=0&src=http%3A%2F%2Fmeiti.fabumao.cn%2F1135652%2F20190202155802951.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3483062554,2520658188&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1486380438,2211423361&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431329&di=cac4c50962f2faf92ce4ff1a29c73583&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.downyi.com%2Fupload%2Fruanjian%2Fhengxiangtupianlunbo-jquery.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3868409151,2088613557&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3440322846,4011652495&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552123051739&di=593148e910dbe846f2d13e3133535d53&imgtype=0&src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F142%2Fd%2F324.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836990267&di=63e8b0140d35d2e9f29008f42f8314bb&imgtype=0&src=http%3A%2F%2Fimages.china.cn%2Fattachement%2Fjpg%2Fsite1000%2F20170924%2F6c0b840a25301b31b4b51a.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2218526674,3033754369&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4266762370,3244059322&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552123051739&di=593148e910dbe846f2d13e3133535d53&imgtype=0&src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F142%2Fd%2F324.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1146748633,629239788&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838192674&di=acf3f54188165758b1286bd34a51f766&imgtype=0&src=http%3A%2F%2Fm.360buyimg.com%2Fn12%2Fg10%2FM00%2F1E%2F1E%2FrBEQWVNt8ZkIAAAAAAEqtQDCwL4AAGEMgAZFzIAASrN125.jpg%2521q70.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2328722018,1154947414&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3726386602,3899424628&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838606438&di=dfbf8d322db2e41fdbc879a5101f726b&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F1c92af15e7e23955b70cd353524222b79704eb7c.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3835009885,1226818710&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2187275096,1491836145&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552720723&di=5af66bfb92d7c10afc1c299fc660156d&imgtype=jpg&er=1&src=http%3A%2F%2Fdown1.sucaitianxia.net%2Feps%2F55%2Feps16077.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2218526674,3033754369&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554433004&di=4430b8447f423cc70925e6f533994629&imgtype=jpg&er=1&src=http%3A%2F%2Fimage.tupian114.com%2F20130110%2F11471928.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4266762370,3244059322&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3750355673,536097549&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552724688&di=10dd45c895ba9a1f9856f691ff7be67a&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F11%2F39%2F47%2F09B58PICPVq.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1486380438,2211423361&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2108637241,1993954056&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1938756251,2755438251&fm=11&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836990267&di=63e8b0140d35d2e9f29008f42f8314bb&imgtype=0&src=http%3A%2F%2Fimages.china.cn%2Fattachement%2Fjpg%2Fsite1000%2F20170924%2F6c0b840a25301b31b4b51a.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3868409151,2088613557&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3440322846,4011652495&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552123051739&di=593148e910dbe846f2d13e3133535d53&imgtype=0&src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F142%2Fd%2F324.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4228542970,1725444700&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1072325142,3221163279&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554433004&di=4430b8447f423cc70925e6f533994629&imgtype=jpg&er=1&src=http%3A%2F%2Fimage.tupian114.com%2F20130110%2F11471928.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1228574751,2869847369&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3483062554,2520658188&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553838606438&di=dfbf8d322db2e41fdbc879a5101f726b&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F1c92af15e7e23955b70cd353524222b79704eb7c.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1146748633,629239788&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2328722018,1154947414&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431945&di=42b88ab656ef19ce3a8a2627c35376eb&imgtype=jpg&er=1&src=http%3A%2F%2Fimage5.huangye88.com%2F2014%2F11%2F01%2F675f3ec50625b79b.JPG");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3726386602,3899424628&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4266762370,3244059322&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3750355673,536097549&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554431329&di=cac4c50962f2faf92ce4ff1a29c73583&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.downyi.com%2Fupload%2Fruanjian%2Fhengxiangtupianlunbo-jquery.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2108637241,1993954056&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1938756251,2755438251&fm=11&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836938494&di=fc454c0d01a28f0fedd3a266ea3e22ee&imgtype=0&src=http%3A%2F%2Fimg009.hc360.cn%2Fm2%2FM01%2FE5%2FF2%2FwKhQcVRBn_mEWXTKAAAAAKh0wO4790.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3835009885,1226818710&fm=26&gp=0.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2187275096,1491836145&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2496089338,1929248438&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552720723&di=5af66bfb92d7c10afc1c299fc660156d&imgtype=jpg&er=1&src=http%3A%2F%2Fdown1.sucaitianxia.net%2Feps%2F55%2Feps16077.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1228574751,2869847369&fm=26&gp=0.jpg");
        urlList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3483062554,2520658188&fm=26&gp=0.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553836422534&di=a4bdb33ec929f99be6e6f3e6c21edc26&imgtype=0&src=http%3A%2F%2Fimg3.redocn.com%2Ftupian%2F20150606%2Fhengxiangmubantietu_3761982.jpg");
        urlList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1486380438,2211423361&fm=26&gp=0.jpg");
        urlList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3868409151,2088613557&fm=26&gp=0.jpg");
        urlList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3440322846,4011652495&fm=26&gp=0.jpg");
        return urlList;
    }
}
