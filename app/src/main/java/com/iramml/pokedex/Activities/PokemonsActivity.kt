package com.iramml.pokedex.Activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.Gson
import com.iramml.pokedex.Common.Common
import com.iramml.pokedex.Interfaces.HttpRequest
import com.iramml.pokedex.Model.PokemonsResponse
import com.iramml.pokedex.R
import com.iramml.pokedex.Util.Network
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.iram.check_ins.RecyclerViewMain.ClickListener
import com.example.iram.check_ins.RecyclerViewMain.customAdapter


class PokemonsActivity : AppCompatActivity() {
    var network:Network?=null
    var listPokemons:RecyclerView?=null
    var adapterList:customAdapter?=null
    var toolbar: Toolbar?=null
    companion object {
        val KEY_URL="com.iramml.pokedex.Activities.url"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemons)
        network= Network(this)

        initToolbar()
        initRecyclerView()
        getPokemons()
    }
    fun initToolbar(){
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
    fun initRecyclerView(){
        listPokemons=findViewById(R.id.listPokemons)
        listPokemons?.setHasFixedSize(true)
        listPokemons?.setLayoutManager(GridLayoutManager(this, 3))

    }
    fun getPokemons(){
        var url="${Common.BASE_URL}/api/v2/pokemon/"
        Log.d("URL", url)
        network?.httpRequest(url, object:HttpRequest{
            override fun httpResponseSuccess(response: String) {
                Log.d("HTTP_RESPONSE", response)
                var gson=Gson()
                var responseObject=gson.fromJson(response, PokemonsResponse::class.java)
                for(i in 1..responseObject.results.size-20)
                    responseObject.results[i-1].url_sprite="${Common.BASE_URL_SPRITES}PokeAPI/sprites/master/sprites/pokemon/$i.png"
                implementRecyclerView(responseObject)
            }
        })
    }
    fun implementRecyclerView(responseObject:PokemonsResponse){
        adapterList= customAdapter(responseObject.results, object :ClickListener{
            override fun onClick(view: View, index: Int) {
                val intent= Intent(applicationContext, PokemonDetailActivity::class.java)
                Log.d("URL_ACTIVITY", responseObject.results[index].url)
                intent.putExtra(KEY_URL, adapterList?.items!![index].url)
                startActivity(intent)
            }
        })
        listPokemons?.adapter=adapterList
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pokemons_menu,menu)
        val searchManager=getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val itemBusqueda=menu?.findItem(R.id.searchView)
        val searchView= itemBusqueda?.actionView as android.support.v7.widget.SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint="Search pokemon..."

        searchView.setOnQueryTextFocusChangeListener{ view: View, b: Boolean ->
            //preparar datos
        }
        searchView.setOnQueryTextListener(object: android.support.v7.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapterList?.filter(p0!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapterList?.filter(p0!!)
                return true
            }

        })
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}