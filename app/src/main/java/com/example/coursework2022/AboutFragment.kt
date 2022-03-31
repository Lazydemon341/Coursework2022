package com.example.coursework2022

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vansuita.materialabout.builder.AboutBuilder

class AboutFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return AboutBuilder.with(requireContext())
      //.setPhoto(R.mipmap.ic_launcher)
      //.setCover(R.mipmap.profile_cover)
      .setName("Vlasyuk Alexander")
      .setSubTitle("Android developer, Moscow")
      .addEmailLink("vlas.s.341@gmail.com")
      .addGitHubLink("Lazydemon341")
      .setAppIcon(R.mipmap.ic_launcher)
      .setAppName(R.string.app_name)
      .addFiveStarsAction()
      .setVersionNameAsAppSubTitle()
      .addShareAction(R.string.app_name)
      .setWrapScrollView(true)
      .setLinksAnimated(true)
      .setShowAsCard(true)
      .build();
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

//    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
//      title = getString(R.string.action_about)
//      setDisplayHomeAsUpEnabled(true)
//    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

//  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//    super.onCreateOptionsMenu(menu, inflater)
//    menu.clear()
//  }

  companion object {
    fun newInstance() = AboutFragment()
  }
}