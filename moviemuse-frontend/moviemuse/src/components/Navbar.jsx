import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <div className="container navbar-container">
        <Link to="/" className="navbar-logo">
          MovieMuse
        </Link>
        <div className="navbar-links">
          <Link to="/" className="navbar-link">
            Home
          </Link>
          <Link to="/categories" className="navbar-link">
            Categories
          </Link>
          {/* <Link to="/add-movie" className="navbar-link">Add Movie</Link> maybe later */}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;