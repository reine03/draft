from flask import Flask, render_template
from database.models import db  # Siguraduhing tama ang import path ng db mo
from routes.camera import camera_bp

app = Flask(__name__)

# I-configure ang iyong Database (Palitan ang URI depende sa gamit mo, ex: SQLite)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///cctv.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# I-initialize ang db kasama ang app
db.init_app(app)

# I-register ang blueprint mula sa routes
app.register_blueprint(camera_bp)

@app.route('/')
def index():
    # I-render ang frontend dashboard
    return render_template('index.html')

if __name__ == '__main__':
    # Gumawa ng database tables kung wala pa
    with app.app_context():
        db.create_all()
        
    app.run(debug=True, port=5000)