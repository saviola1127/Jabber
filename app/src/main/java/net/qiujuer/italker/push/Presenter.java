package net.qiujuer.italker.push;

import android.text.TextUtils;

public class Presenter implements IPresenter {

    private IView mView;

    public Presenter(IView view) {
        mView = view;
    }


    @Override
    public void submit() {
        String input = mView.getInput();
        if (!TextUtils.isEmpty(input)) {

            IUserService service = new UserService();
            String result = "Result ：" + service.search(input.hashCode());

            //deal with UI related changes
            mView.setResult(result);

        } else {
            return;
        }
    }
}
