package net.qiujuer.italker.push;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import net.qiujuer.italker.push.common.app.Activity;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements IView {

    @BindView(R.id.txt_result)
    TextView mResultText;

    @BindView(R.id.edit_query)
    EditText mInputTxt;

    private IPresenter mPresenter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter = new Presenter(this);
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        mPresenter.submit();
    }

    @Override
    public String getInput() {
        return String.valueOf(mInputTxt.getText());
    }

    @Override
    public void setResult(String txt) {
        mResultText.setText(txt);
    }
}
