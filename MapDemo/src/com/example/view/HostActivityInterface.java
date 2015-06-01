package com.example.view;

public interface HostActivityInterface {

	public void setSelectedFragment(BaseFragment fragment);

	public void popBackStack();

	public void popBackStackTillTag(String tag);

	public void addFragment(BaseFragment fragment);

	public void addMultipleFragments(BaseFragment fragments[]);
}
