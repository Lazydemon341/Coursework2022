package com.example.coursework2022.features.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.coursework2022.R
import com.example.coursework2022.R.string
import com.vansuita.materialabout.builder.AboutBuilder

class AboutFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return AboutBuilder.with(requireContext())
      .setPhoto(R.drawable.photo)
      .setCover(R.mipmap.profile_cover)
      .setName("Vlasyuk Alexander")
      .setSubTitle("Android developer, Moscow")
      .addEmailLink("vlas.s.341@gmail.com")
      .addGitHubLink("Lazydemon341")
      .setAppIcon(R.drawable.ic_launcher)
      .setAppName(string.app_name)
      .addFiveStarsAction()
      .setVersionNameAsAppSubTitle()
      .addShareAction(string.app_name)
      .setWrapScrollView(true)
      .setLinksAnimated(true)
      .setShowAsCard(true)
      .build();
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  companion object {
    fun newInstance() = AboutFragment()
  }
}