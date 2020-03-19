uniform vec2 UNIFORMS;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	vec3 normal = gl_NormalMatrix * gl_Normal;
	
	float diffuseDot = max(dot(normal, vec3(gl_LightSource[0].position)), 0.0);
	float specularDot = max(dot(normal, vec3(gl_LightSource[0].halfVector)), 0.0);
	specularDot = pow(specularDot, UNIFORMS.y);
	
	// Normalize position, to get a {-1..1} value for each vertex.
	// Multiply with current sin.
	vec3 color3D = normalize(vec3(gl_Vertex)) * UNIFORMS.x;
	// {-1..1} => {0..1} & Intensify colors.
	color3D = (color3D * 0.5 + 0.5) * 2.0;
	
	// Accumulate color contributions.
	color3D = diffuseDot * color3D + vec3(gl_LightModel.ambient);
	gl_FrontColor.rgb = specularDot * vec3(gl_LightSource[0].specular) + color3D;
	gl_FrontColor.a = 1.0;
}