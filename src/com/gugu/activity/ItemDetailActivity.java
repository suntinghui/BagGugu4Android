package com.gugu.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.ares.baggugu.dto.app.DebtPackageInfoAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.ItemDetailInfoLayout;
import com.gugu.utils.AdapterUtil;

/**
 * 产品详情
 * 
 * @author sth
 * 
 */
public class ItemDetailActivity extends BaseActivity implements OnClickListener {

	private GridView gridView = null;

	private DebtPackageInfoAppDto infoDto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item_detail);

		infoDto = (DebtPackageInfoAppDto) this.getIntent().getSerializableExtra("DTO");

		initView();
	}

	private void initView() {
		Button backBtn = (Button) this.findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);

		((TextView) this.findViewById(R.id.titleTextView)).setText("产品详情");

		gridView = (GridView) this.findViewById(R.id.gridView1);
		gridView.setAdapter(new GridViewAdapter(this));
	}

	private class GridViewAdapter extends BaseAdapter {
		private Context context;

		public GridViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return 11;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 2 || position == 9) {
				View view = new View(context);
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AdapterUtil.dip2px(context, 15));
				view.setLayoutParams(params);
				view.setBackgroundColor(Color.parseColor("#f3f3f3"));
				return view;

			} else {
				ItemDetailInfoLayout layout = new ItemDetailInfoLayout(context, infoDto);
				switch (position) {
				case 0:
					layout.setValue("债权编码", infoDto.getNum());
					break;

				case 1:
					layout.setValue("债权额度", infoDto.getTotalMoney() + "元");
					break;

				case 3:
					// 类型 a定投 b抢投 c转让
					if (infoDto.getType() == 'b') {
						layout.setValue("债权周期", infoDto.getMinPeriod() + "天 - " + infoDto.getMaxPeriod() + "天");
					} else {
						layout.setValue("债权周期", infoDto.getMaxPeriod() + "天");
					}

					break;

				case 4:
					// 类型 a定投 b抢投 c转让
					if (infoDto.getType() == 'b') {
						layout.setValue("预期年化利率", infoDto.getMaxRate() + "%" + " - " + infoDto.getMinRate() + "%");
					} else {
						layout.setValue("预期年化利率", infoDto.getMaxRate() + "%");
					}

					break;

				case 5:
					layout.setValue("保障方式", "100％本息垫付，第三方再保");
					break;

				case 6:
					if (infoDto.getLimitCount() == 0) {
						layout.setValue("投资限额", "￥" + infoDto.getLimitMoney() + "/份，不限份数");
					} else {
						layout.setValue("投资限额", "￥" + infoDto.getLimitMoney() + "/份，最多可购买" + infoDto.getLimitCount() + "份");
					}
					break;

				case 7: // 管理费率
					layout.setManageRate();
					break;

				case 8: // 提前赎回转让费
					layout.setTransferRate();
					break;

//				case 10: // 红包说明
//					layout.setLuckMoney();
//					break;
//
//				case 11: // 第三方担保协议
//					layout.setGuarantee();
//					break;

				case 10: // 赎回方式
					layout.setValue("赎回方式", "按天付息，到期还本，资金直接转入鼓鼓理财投资账户");
					break;
				}

				return layout;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;
		}
	}

}
