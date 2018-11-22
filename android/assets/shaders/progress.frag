#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_progress;

void main() {

	vec4 texColor = texture2D(u_texture, v_texCoords);


	
	float p = v_texCoords.y;
	
	if(p >  u_progress){
		texColor.a = 1.0 * texColor.a;
	}else{
		texColor.a = 0.0;
	}
	

	
	gl_FragColor = v_color * texColor;
}
