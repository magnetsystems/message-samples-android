package com.magnet.magnetchat.mvp.api;

import java.util.List;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public interface IListView<T> extends IBaseView {
  void showList(List<T> list, boolean toAppend);
}
