package com.example.okrapp.di

import com.example.okrapp.BuildConfig
import com.example.okrapp.domain.GetPopularPhotosUseCase
import com.example.okrapp.remote.FHPRepository
import com.example.okrapp.remote.NetworkDataSource
import com.example.okrapp.remote.interfaces.RemoteDataSource
import com.example.okrapp.remote.interfaces.Repository
import com.example.okrapp.remote.interfaces.WebService
import com.example.okrapp.remote.network.FiveHundredPixelsAPI
import com.example.okrapp.remote.network.RetrofitWebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@InstallIn(ActivityComponent::class)
@Module
object HiltDependenciesModule {


    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun provideOKHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient().apply {
        OkHttpClient.Builder().run {
            addInterceptor(loggingInterceptor)
            build()
        }
    }

    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory = MoshiConverterFactory.create()

    @Provides
    fun provideRetrofitInstance(
        client: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): FiveHundredPixelsAPI =
        Retrofit.Builder().run {
            baseUrl(BuildConfig.BASE_URL)
            addConverterFactory(moshiConverterFactory)
            client(client)
            build()
        }.run {
            create(FiveHundredPixelsAPI::class.java)
        }


    @Provides
    fun providesRetrofitService(retrofitClient: FiveHundredPixelsAPI): WebService =
        RetrofitWebService(retrofitClient)

    @Provides
    fun providesNetworkDataSource(webService: WebService): RemoteDataSource =
        NetworkDataSource(webService)

    @Provides
    fun provideRepository(remoteDataSource: RemoteDataSource): Repository =
        FHPRepository(remoteDataSource)

    @Provides
    fun provideUseCase(repository: Repository): GetPopularPhotosUseCase =
        GetPopularPhotosUseCase(
            repository
        )
}