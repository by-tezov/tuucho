package com.tezov.tuucho.demo.di

object RetrofitModule {

    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://your-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun providePageNetworkService(retrofit: Retrofit): PageNetworkService =
        retrofit.create(PageNetworkService::class.java)

    fun provideRepository(pageNetworkService: PageNetworkService): PageDataResponse =
        PageRepositoryImpl(pageNetworkService)

}