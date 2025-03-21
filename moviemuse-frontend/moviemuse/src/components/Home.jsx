import React from 'react'

const Home = () => {
  return (
    <div className='home'>
      <div className='container'>
        <div className='home-header'>
            <h1 className='page-title'>MovieMuse</h1>
            <p className='page-subtitle'>My Movie Collection</p>
        </div>
        <section className='movie-section'>
            <h2 className='section-title'>All Movies</h2>
            <div className='movies-grid'></div>
        </section>
      </div>
    </div>
  )
}

export default Home
