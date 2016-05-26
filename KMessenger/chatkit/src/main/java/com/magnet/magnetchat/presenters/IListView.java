package com.magnet.magnetchat.presenters;

import java.util.List;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
@Deprecated
public interface IListView<T> extends IBaseView {
  void showList(List<T> list, boolean toAppend);
}
