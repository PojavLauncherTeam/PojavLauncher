uniform vec4 UNIFORMS;

varying vec2 dots;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	vec3 normal = gl_NormalMatrix * gl_Normal;
	
	// Pass the dot products to the fragment shader.
	dots.x = max(dot(normal, vec3(gl_LightSource[0].position)), 0.0);
	dots.y = max(dot(normal, vec3(gl_LightSource[0].halfVector)), 0.0);
}